package com.iisovaii.employee_bff.kafka;


import com.iisovaii.employee_bff.dto.kafka.OperationResultMessage;
import com.iisovaii.employee_bff.dto.operation.OperationDto;
import com.iisovaii.employee_bff.dto.ws.WsBalanceEvent;
import com.iisovaii.employee_bff.dto.ws.WsOperationEvent;
import com.iisovaii.employee_bff.service.FcmNotificationService;
import com.iisovaii.employee_bff.ws.WsSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OperationResultConsumer {

    private static final String TOPIC = "operations-results";

    private final SimpMessagingTemplate messagingTemplate;
    private final WsSessionRegistry wsSessionRegistry;
    private final FcmNotificationService fcmNotificationService;

    @KafkaListener(
            topics = TOPIC,
            groupId = "employee-backend-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(OperationResultMessage message) {
        log.debug(
                "Получен результат операции {} со статусом {}",
                message.getOperationId(),
                message.getStatus()
        );

        if (message.getAccountId() == null) {
            log.warn("Пропущено сообщение без accountId: operationId={}", message.getOperationId());
            return;
        }

        wsSessionRegistry
                .getUserIdByAccountId(message.getAccountId())
                .ifPresent(userId -> {

                    WsOperationEvent operationEvent = buildOperationEvent(message);
                    messagingTemplate.convertAndSendToUser(
                            userId.toString(),
                            "/queue/operations/" + message.getAccountId(),
                            operationEvent
                    );

                    if ("SUCCESS".equals(message.getStatus()) && message.getNewBalance() != null) {
                        WsBalanceEvent balanceEvent = buildBalanceEvent(message);
                        messagingTemplate.convertAndSendToUser(
                                userId.toString(),
                                "/queue/balance/" + message.getAccountId(),
                                balanceEvent
                        );
                    }
                });

        // FCM push-уведомление всем сотрудникам
        String accountDesc = message.getAccountId() != null ? message.getAccountId().toString() : "—";
        String status = "SUCCESS".equals(message.getStatus()) ? "выполнена" : "не выполнена";
        fcmNotificationService.sendToAllEmployees(
                "Новая операция",
                "Операция по счёту " + accountDesc + " " + status
        );
    }

    private WsOperationEvent buildOperationEvent(OperationResultMessage message) {
        OperationDto operationDto = new OperationDto();
        operationDto.setOperationId(message.getOperationId());
        operationDto.setAmount(message.getAmount());
        operationDto.setStatus("SUCCESS".equals(message.getStatus())
                ? OperationDto.OperationStatus.SUCCESS
                : OperationDto.OperationStatus.FAILED);
        operationDto.setFailReason(message.getErrorMessage());

        if (message.getCurrency() != null) {
            try {
                operationDto.setCurrency(OperationDto.Currency.valueOf(message.getCurrency()));
            } catch (IllegalArgumentException e) {
                log.warn("Неизвестная валюта: {}", message.getCurrency());
            }
        }

        if (message.getType() != null) {
            try {
                operationDto.setType(OperationDto.OperationType.valueOf(message.getType()));
            } catch (IllegalArgumentException e) {
                log.warn("Неизвестный тип операции: {}", message.getType());
            }
        }

        operationDto.setCreatedAt(message.getCreatedAt());

        WsOperationEvent event = new WsOperationEvent();
        event.setType("SUCCESS".equals(message.getStatus())
                ? WsOperationEvent.WsEventType.OPERATION_ADDED
                : WsOperationEvent.WsEventType.OPERATION_UPDATED
        );
        event.setOperation(operationDto);

        return event;
    }

    private WsBalanceEvent buildBalanceEvent(OperationResultMessage message) {
        WsBalanceEvent event = new WsBalanceEvent();
        event.setType(WsOperationEvent.WsEventType.BALANCE_UPDATED);
        event.setAccountId(message.getAccountId());
        event.setNewBalance(message.getNewBalance());

        if (message.getCurrency() != null) {
            try {
                event.setCurrency(OperationDto.Currency.valueOf(message.getCurrency()));
            } catch (IllegalArgumentException e) {
                log.warn("Неизвестная валюта для balance event: {}", message.getCurrency());
            }
        }

        return event;
    }
}