package com.iisovaii.client_bff.config;

import com.iisovaii.client_bff.security.JwtValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.Objects;

// проверяет JWT в STOMP заголовке при CONNECT
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtValidator jwtValidator;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String userId = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");
            if (userId == null) {
                throw new MessagingException("WS: userId не найден в сессии");
            }
            // устанавливаем Principal чтобы работал /user/queue/...
            accessor.setUser(new StompPrincipal(userId));
        }

        return message;
    }
}
