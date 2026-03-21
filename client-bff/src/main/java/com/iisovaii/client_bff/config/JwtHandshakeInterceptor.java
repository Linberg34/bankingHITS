package com.iisovaii.client_bff.config;

import com.iisovaii.client_bff.security.JwtValidator;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

// проверяет токен при HTTP Upgrade -> WebSocket
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtValidator jwtValidator;

    @Override
    public boolean beforeHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes) {

        // токен приходит в cookie при WS handshake
        if (request instanceof ServletServerHttpRequest servletRequest) {
            Cookie[] cookies = servletRequest.getServletRequest().getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("access_token".equals(cookie.getName())) {
                        try {
                            Claims claims = jwtValidator.validate(cookie.getValue());
                            // кладём userId в атрибуты сессии
                            attributes.put("userId", claims.getSubject());
                            return true;
                        } catch (Exception e) {
                            return false; // отклоняем handshake
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            Exception exception) {}
}
