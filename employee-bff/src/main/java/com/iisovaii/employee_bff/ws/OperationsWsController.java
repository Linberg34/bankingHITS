package com.iisovaii.employee_bff.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OperationsWsController {

    private final WsSessionRegistry sessionRegistry;

    // Angular подписывается:
    // stompClient.subscribe('/user/queue/operations/{accountId}', ...)
    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();

        if (destination == null || sessionId == null) {
            return;
        }

        // обрабатываем только подписки на операции по счёту
        if (destination.startsWith("/user/queue/operations/")) {
            try {
                String accountIdStr = destination
                        .replace("/user/queue/operations/", "");
                UUID accountId = UUID.fromString(accountIdStr);
                UUID userId = extractUserId(accessor);

                if (userId != null) {
                    sessionRegistry.register(accountId, sessionId, userId);
                    log.debug(
                            "Сотрудник {} подписался на счёт {}",
                            userId,
                            accountId
                    );
                }

            } catch (IllegalArgumentException e) {
                log.warn(
                        "Некорректный accountId в destination: {}",
                        destination
                );
            }
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();

        if (sessionId != null) {
            sessionRegistry.removeSession(sessionId);
            log.debug("WS сессия {} отключена", sessionId);
        }
    }

    private UUID extractUserId(StompHeaderAccessor accessor) {
        // Principal устанавливается в JwtChannelInterceptor при CONNECT
        if (accessor.getUser() == null) {
            return null;
        }
        try {
            return UUID.fromString(accessor.getUser().getName());
        } catch (IllegalArgumentException e) {
            log.warn(
                    "Не удалось распарсить userId из Principal: {}",
                    accessor.getUser().getName()
            );
            return null;
        }
    }
}