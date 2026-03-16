package com.iisovaii.employee_bff.config;

import com.iisovaii.employee_bff.security.JwtValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtValidator jwtValidator;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // префикс для сообщений от сервера к клиенту
        registry.enableSimpleBroker("/queue", "/topic");
        // префикс для сообщений от клиента к серверу
        registry.setApplicationDestinationPrefixes("/app");
        // для персональных очередей /user/queue/...
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:4201")
                .addInterceptors(new JwtHandshakeInterceptor(jwtValidator))
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration) {
        registration.interceptors(new JwtChannelInterceptor());
    }

    // проверяет JWT при HTTP Upgrade -> WebSocket
    private static class JwtHandshakeInterceptor
            implements org.springframework.web.socket.server.HandshakeInterceptor {

        private final JwtValidator jwtValidator;

        JwtHandshakeInterceptor(JwtValidator jwtValidator) {
            this.jwtValidator = jwtValidator;
        }

        @Override
        public boolean beforeHandshake(
                org.springframework.http.server.ServerHttpRequest request,
                org.springframework.http.server.ServerHttpResponse response,
                org.springframework.web.socket.WebSocketHandler wsHandler,
                java.util.Map<String, Object> attributes) {

            if (request instanceof
                    org.springframework.http.server.ServletServerHttpRequest
                            servletRequest) {

                jakarta.servlet.http.Cookie[] cookies =
                        servletRequest.getServletRequest().getCookies();

                if (cookies != null) {
                    for (jakarta.servlet.http.Cookie cookie : cookies) {
                        if ("access_token".equals(cookie.getName())) {
                            try {
                                io.jsonwebtoken.Claims claims =
                                        jwtValidator.validate(cookie.getValue());
                                attributes.put(
                                        "userId",
                                        claims.getSubject()
                                );
                                return true;
                            } catch (Exception e) {
                                return false;
                            }
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void afterHandshake(
                org.springframework.http.server.ServerHttpRequest request,
                org.springframework.http.server.ServerHttpResponse response,
                org.springframework.web.socket.WebSocketHandler wsHandler,
                Exception exception) {}
    }

    // проверяет JWT в STOMP CONNECT фрейме
    private static class JwtChannelInterceptor
            implements ChannelInterceptor {

        @Override
        public Message<?> preSend(
                Message<?> message,
                MessageChannel channel) {

            StompHeaderAccessor accessor =
                    MessageHeaderAccessor.getAccessor(
                            message,
                            StompHeaderAccessor.class
                    );

            if (accessor != null &&
                    StompCommand.CONNECT.equals(accessor.getCommand())) {

                String userId = (String) accessor
                        .getSessionAttributes()
                        .get("userId");

                if (userId == null) {
                    throw new org.springframework.messaging.MessagingException(
                            "WS: userId не найден в сессии"
                    );
                }

                // устанавливаем Principal чтобы работал /user/queue/...
                accessor.setUser(new StompPrincipal(userId));
            }

            return message;
        }
    }
}