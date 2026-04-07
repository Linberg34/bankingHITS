package com.iisovaii.employee_bff.config;

import com.iisovaii.employee_bff.security.JwtValidator;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtValidator jwtValidator;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Без SockJS — фронтенд подключается через raw WebSocket (ws://)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:4200", "http://localhost:4201", "http://localhost:4202")
                .addInterceptors(new JwtHandshakeInterceptor());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new JwtChannelInterceptor(jwtValidator));
    }

    // Разрешает handshake для всех; аутентификация в JwtChannelInterceptor
    private static class JwtHandshakeInterceptor implements HandshakeInterceptor {

        @Override
        public boolean beforeHandshake(
                @NonNull org.springframework.http.server.ServerHttpRequest request,
                @NonNull org.springframework.http.server.ServerHttpResponse response,
                @NonNull org.springframework.web.socket.WebSocketHandler wsHandler,
                @NonNull Map<String, Object> attributes) {
            return true;
        }

        @Override
        public void afterHandshake(
                @NonNull org.springframework.http.server.ServerHttpRequest request,
                @NonNull org.springframework.http.server.ServerHttpResponse response,
                @NonNull org.springframework.web.socket.WebSocketHandler wsHandler,
                Exception exception) {}
    }

    // Проверяет JWT из STOMP Authorization заголовка при CONNECT
    private static class JwtChannelInterceptor implements ChannelInterceptor {

        private final JwtValidator jwtValidator;

        JwtChannelInterceptor(JwtValidator jwtValidator) {
            this.jwtValidator = jwtValidator;
        }

        @Override
        public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
            StompHeaderAccessor accessor =
                    MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

            if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                String authHeader = accessor.getFirstNativeHeader("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    throw new MessagingException("WS: отсутствует Authorization заголовок");
                }
                String token = authHeader.substring(7);
                try {
                    Claims claims = jwtValidator.validate(token);
                    String userId = claims.getSubject();
                    accessor.setUser(new StompPrincipal(userId));
                } catch (Exception e) {
                    throw new MessagingException("WS: невалидный токен: " + e.getMessage());
                }
            }

            return message;
        }
    }
}
