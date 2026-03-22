package com.iisovaii.client_bff.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {

    // interceptor прокидывает JWT из текущего SecurityContext
    // во все исходящие Feign запросы к доменным сервисам
    @Bean
    public RequestInterceptor jwtRelayInterceptor() {
        return requestTemplate -> {
            Authentication auth =
                    SecurityContextHolder.getContext().getAuthentication();

            String token = null;
            if (auth != null && auth.getCredentials() instanceof String credentialsToken) {
                token = credentialsToken;
            }

            // fallback: если SecurityContext пустой/без credentials, берём токен из текущего HTTP запроса
            if (token == null) {
                token = extractTokenFromCurrentRequest();
            }

            if (token != null && !token.isBlank()) {
                requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
        };
    }

    private String extractTokenFromCurrentRequest() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return null;
        }

        HttpServletRequest request = attrs.getRequest();
        if (request == null) return null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                    return cookie.getValue();
                }
            }
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return null;
    }
}
