package com.iisovaii.employee_bff.security;

import com.iisovaii.employee_bff.exception.JwtExpiredException;
import com.iisovaii.employee_bff.exception.JwtInvalidException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;

// security/JwtValidator.java
@Component
@Slf4j
public class JwtValidator {

    private final String jwksUrl;
    private volatile JWKSet jwkSet;

    public JwtValidator(@Value("${sso.jwks-url}") String jwksUrl) {
        this.jwksUrl = jwksUrl;
        // не загружаем при старте — загружаем при первом запросе
    }

    public Claims validate(String token) {
        try {
            RSAPublicKey publicKey = getPublicKey();

            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException("Токен истёк");
        } catch (Exception e) {
            throw new JwtInvalidException(
                    "Невалидный токен: " + e.getMessage()
            );
        }
    }

    private RSAPublicKey getPublicKey() {
        // lazy init — загружаем при первом вызове
        if (jwkSet == null) {
            synchronized (this) {
                if (jwkSet == null) {
                    loadJwks();
                }
            }
        }
        try {
            RSAKey rsaKey = (RSAKey) jwkSet.getKeys().get(0);
            return rsaKey.toRSAPublicKey();
        } catch (Exception e) {
            throw new JwtInvalidException(
                    "Ошибка получения публичного ключа: " + e.getMessage()
            );
        }
    }

    private void loadJwks() {
        try {
            log.info("Загружаем JWKS с {}", jwksUrl);
            this.jwkSet = JWKSet.load(new URL(jwksUrl));
            log.info("JWKS успешно загружен");
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Не удалось загрузить JWKS с " + jwksUrl, e
            );
        }
    }

    // вызывать если нужно обновить ключи (ротация ключей в SSO)
    public void refreshJwks() {
        synchronized (this) {
            jwkSet = null;
        }
    }
}