package com.iisovaii.client_bff.kafka;

import com.iisovaii.client_bff.dto.ws.WsBalanceEvent;
import com.iisovaii.client_bff.dto.ws.WsEventType;
import com.iisovaii.client_bff.dto.ws.WsOperationDto;
import com.iisovaii.client_bff.dto.ws.WsOperationEvent;
import com.iisovaii.client_bff.service.FcmNotificationService;
import com.iisovaii.client_bff.ws.OperationsWsController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class OperationResultConsumer {

    private static final String OPERATIONS_RESULTS_TOPIC = "operations-results";

    private final OperationsWsController operationsWsController;
    private final FcmNotificationService fcmNotificationService;

    public OperationResultConsumer(
            OperationsWsController operationsWsController,
            FcmNotificationService fcmNotificationService
    ) {
        this.operationsWsController = operationsWsController;
        this.fcmNotificationService = fcmNotificationService;
    }

    @KafkaListener(
            topics = OPERATIONS_RESULTS_TOPIC,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOperationResult(OperationResultMessage message) {
        UUID userId = message.getUserId();
        UUID accountId = message.getAccountId();

        if (userId == null || accountId == null) {
            log.warn("Пропущено сообщение без userId или accountId: operationId={}", message.getOperationId());
            return;
        }

        log.debug("WS push: operationId={} status={} account={}", message.getOperationId(), message.getStatus(), accountId);

        // событие по операции
        if (message.getOperationId() != null) {
            WsOperationDto dto = new WsOperationDto(
                    message.getOperationId(),
                    message.getType(),
                    message.getAmount(),
                    message.getCurrency(),
                    message.getAccountNumber() != null ? message.getAccountNumber() : accountId.toString(),
                    message.getStatus(),
                    message.getErrorMessage(),
                    message.getCreatedAt()
            );
            WsEventType eventType = "SUCCESS".equals(message.getStatus())
                    ? WsEventType.OPERATION_ADDED
                    : WsEventType.OPERATION_UPDATED;

            operationsWsController.sendOperationEvent(
                    userId.toString(),
                    accountId,
                    new WsOperationEvent(eventType, dto)
            );
        }

        // событие по балансу при успехе
        if ("SUCCESS".equals(message.getStatus()) && message.getNewBalance() != null && message.getCurrency() != null) {
            operationsWsController.sendBalanceEvent(
                    userId.toString(),
                    accountId,
                    new WsBalanceEvent(WsEventType.BALANCE_UPDATED, accountId, message.getNewBalance(), message.getCurrency())
            );
        }

        // FCM push-уведомление владельцу счёта
        String opType = message.getType() != null ? message.getType() : "Операция";
        String account = message.getAccountNumber() != null ? message.getAccountNumber() : accountId.toString();
        if ("SUCCESS".equals(message.getStatus())) {
            fcmNotificationService.sendToUser(userId, "Новая операция", opType + " по счёту " + account + " выполнена");
        } else {
            fcmNotificationService.sendToUser(userId, "Ошибка операции", opType + " по счёту " + account + " не выполнена");
        }
    }
}
