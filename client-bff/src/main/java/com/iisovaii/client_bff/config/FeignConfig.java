package com.iisovaii.client_bff.config;

import com.iisovaii.client_bff.infrastructure.trace.TraceContextHolder;
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

            relayHeader(requestTemplate, TraceContextHolder.TRACE_HEADER, TraceContextHolder.traceId());
            relayHeader(requestTemplate, TraceContextHolder.APP_HEADER, TraceContextHolder.appSource());

            String idempotencyKey = extractHeaderFromCurrentRequest(TraceContextHolder.IDEMPOTENCY_HEADER);
            if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                relayHeader(requestTemplate, TraceContextHolder.IDEMPOTENCY_HEADER, idempotencyKey);
            }
        };
    }

    private String extractTokenFromCurrentRequest() {
        String authorization = extractHeaderFromCurrentRequest(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

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

        return null;
    }

    private String extractHeaderFromCurrentRequest(String headerName) {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return null;
        }

        HttpServletRequest request = attrs.getRequest();
        return request != null ? request.getHeader(headerName) : null;
    }

    private void relayHeader(feign.RequestTemplate requestTemplate, String headerName, String value) {
        if (value != null && !value.isBlank()) {
            requestTemplate.header(headerName, value);
        }
    }
}
