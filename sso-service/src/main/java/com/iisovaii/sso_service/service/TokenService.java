package com.iisovaii.sso_service.service;

import com.iisovaii.sso_service.domain.Role;
import com.iisovaii.sso_service.domain.SsoUser;
import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final RSAKey rsaKey;

    @Value("${sso.issuer}")
    private String issuer;

    @Value("${sso.access-token-expiry-seconds:3600}")
    private long expirySeconds;

    // service/TokenService.java — в generateToken roles теперь енамы
    public String generateToken(SsoUser user) {
        try {
            RSAPrivateKey privateKey = rsaKey.toRSAPrivateKey();

            Date now = new Date();
            Date expiry = new Date(
                    now.getTime() + expirySeconds * 1000
            );

            // конвертируем енамы в строки для JWT claims
            List<String> roleNames = user.getRoles().stream()
                    .map(Role::name)
                    .toList();

            return Jwts.builder()
                    .setSubject(user.getId().toString())
                    .setIssuer(issuer)
                    .setIssuedAt(now)
                    .setExpiration(expiry)
                    .claim("roles", roleNames)
                    .claim("username", user.getUsername())
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Ошибка генерации токена", e
            );
        }
    }
}