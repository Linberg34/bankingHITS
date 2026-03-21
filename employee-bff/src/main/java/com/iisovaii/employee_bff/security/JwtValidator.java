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

@Component
@Slf4j
public class JwtValidator {

    private final JWKSet jwkSet;

    public JwtValidator(@Value("${sso.jwks-url}") String jwksUrl) {
        try {
            this.jwkSet = JWKSet.load(new URL(jwksUrl));
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Не удалось загрузить JWKS с " + jwksUrl, e
            );
        }
    }

    public Claims validate(String token) {
        try {
            RSAKey rsaKey = (RSAKey) jwkSet.getKeys().get(0);
            RSAPublicKey publicKey = rsaKey.toRSAPublicKey();

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
}