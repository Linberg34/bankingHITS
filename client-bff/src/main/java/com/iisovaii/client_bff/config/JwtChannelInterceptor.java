package com.iisovaii.client_bff.config;

import com.iisovaii.client_bff.security.JwtValidator;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

// Проверяет JWT в заголовке Authorization STOMP CONNECT фрейма
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtValidator jwtValidator;

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
