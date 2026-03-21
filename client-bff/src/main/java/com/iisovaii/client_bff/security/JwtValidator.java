package com.iisovaii.client_bff.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPublicKey;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class JwtValidator {

    private final String jwksUrl;
    private final Duration jwksTtl;

    private final AtomicReference<CachedJwks> cache = new AtomicReference<>();

    public JwtValidator(
            @Value("${sso.jwks-url}") String jwksUrl,
            @Value("${sso.jwks-cache-ttl:PT5M}") Duration jwksTtl
    ) {
        this.jwksUrl = Objects.requireNonNull(jwksUrl, "jwksUrl");
        this.jwksTtl = Objects.requireNonNull(jwksTtl, "jwksTtl");
    }

    public Claims validate(String token) {
        try {
            String kid = extractKid(token);
            RSAPublicKey publicKey = resolvePublicKey(kid);

            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("Токен истёк");
        } catch (Exception e) {
            throw new JwtInvalidException("Невалидный токен: " + e.getMessage(), e);
        }
    }

    private String extractKid(String token) {
        try {
            return SignedJWT.parse(token).getHeader().getKeyID();
        } catch (Exception e) {
            throw new JwtInvalidException("Невалидный JWT: не удалось прочитать header", e);
        }
    }

    private RSAPublicKey resolvePublicKey(String kid) throws Exception {
        // 1) пробуем из кеша (или загружаем при пустом/просроченном)
        JWKSet jwkSet = getJwkSet(false);
        RSAPublicKey key = findRsaPublicKey(jwkSet, kid);
        if (key != null) return key;

        // 2) refresh-on-miss: если kid не нашли — обновим JWKS и попробуем ещё раз
        jwkSet = getJwkSet(true);
        key = findRsaPublicKey(jwkSet, kid);
        if (key != null) return key;

        throw new JwtInvalidException("Не найден подходящий ключ в JWKS (kid=" + kid + ")");
    }

    private JWKSet getJwkSet(boolean forceRefresh) throws Exception {
        CachedJwks cached = cache.get();
        Instant now = Instant.now();

        if (!forceRefresh && cached != null && now.isBefore(cached.expiresAt())) {
            return cached.jwkSet();
        }

        synchronized (this) {
            cached = cache.get();
            now = Instant.now();
            if (!forceRefresh && cached != null && now.isBefore(cached.expiresAt())) {
                return cached.jwkSet();
            }

            try {
                JWKSet loaded = JWKSet.load(new URL(jwksUrl));
                cache.set(new CachedJwks(loaded, now.plus(jwksTtl)));
                return loaded;
            } catch (Exception e) {
                if (cached != null) {
                    // Если JWKS временно недоступен — продолжаем жить на старом кеше
                    log.warn("JWKS refresh failed, using cached keys: {}", e.getMessage());
                    return cached.jwkSet();
                }
                throw e;
            }
        }
    }

    private RSAPublicKey findRsaPublicKey(JWKSet jwkSet, String kid) {
        if (jwkSet == null || jwkSet.getKeys() == null || jwkSet.getKeys().isEmpty()) {
            return null;
        }

        for (JWK jwk : jwkSet.getKeys()) {
            if (!(jwk instanceof RSAKey rsaKey)) continue;
            if (kid != null && rsaKey.getKeyID() != null && !kid.equals(rsaKey.getKeyID())) continue;
            try {
                return rsaKey.toRSAPublicKey();
            } catch (Exception ignored) {
                // пробуем следующий ключ
            }
        }

        return null;
    }

    private record CachedJwks(JWKSet jwkSet, Instant expiresAt) {}
}