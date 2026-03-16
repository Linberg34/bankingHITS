package com.iisovaii.client_bff.config;

import com.iisovaii.client_bff.security.JwtValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

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
        // для персональных очередей (/user/queue/...)
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:4200")
                .addInterceptors(new JwtHandshakeInterceptor(jwtValidator))
                .withSockJS(); // fallback для браузеров без WS
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // дополнительная проверка JWT в STOMP CONNECT фрейме
        registration.interceptors(new JwtChannelInterceptor(jwtValidator));
    }
}