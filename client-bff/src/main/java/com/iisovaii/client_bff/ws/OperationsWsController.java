package com.iisovaii.client_bff.ws;

import com.iisovaii.client_bff.dto.ws.WsBalanceEvent;
import com.iisovaii.client_bff.dto.ws.WsOperationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

/**
 * Отвечает за отправку событий по операциям и балансам
 * на персональные STOMP‑очереди клиента.
 * Клиент подписывается на:
 *   /user/queue/operations/{accountId}
 * где user берётся из Principal (см. JwtChannelInterceptor / StompPrincipal),
 * а {accountId} — идентификатор счёта.
 */
@Controller
@RequiredArgsConstructor
public class OperationsWsController {

    private static final String OPERATIONS_DESTINATION_PREFIX = "/queue/operations/";

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Отправка события об операции (создание/изменение) для конкретного пользователя и счёта.
     */
    public void sendOperationEvent(String userId, UUID accountId, WsOperationEvent event) {
        messagingTemplate.convertAndSendToUser(
                userId,
                OPERATIONS_DESTINATION_PREFIX + accountId,
                event
        );
    }

    /**
     * Отправка события об изменении баланса для конкретного пользователя и счёта.
     */
    public void sendBalanceEvent(String userId, UUID accountId, WsBalanceEvent event) {
        messagingTemplate.convertAndSendToUser(
                userId,
                OPERATIONS_DESTINATION_PREFIX + accountId,
                event
        );
    }
}

