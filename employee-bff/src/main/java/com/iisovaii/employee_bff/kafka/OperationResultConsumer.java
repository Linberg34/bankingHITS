package com.iisovaii.employee_bff.kafka;


import com.iisovaii.employee_bff.dto.kafka.OperationResultMessage;
import com.iisovaii.employee_bff.dto.operation.OperationDto;
import com.iisovaii.employee_bff.dto.ws.WsBalanceEvent;
import com.iisovaii.employee_bff.dto.ws.WsOperationEvent;
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

    private static final String TOPIC = "account.operations.result";

    private final SimpMessagingTemplate messagingTemplate;
    private final WsSessionRegistry wsSessionRegistry;

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

        // находим userId по accountId из реестра сессий
        wsSessionRegistry
                .getUserIdByAccountId(message.getOperationId())
                .ifPresent(userId -> {

                    // пушим событие операции
                    WsOperationEvent operationEvent = buildOperationEvent(message);
                    messagingTemplate.convertAndSendToUser(
                            userId.toString(),
                            "/queue/operations/" + message.getOperationId(),
                            operationEvent
                    );

                    // если операция успешна — пушим обновление баланса
                    if (message.getStatus() ==
                            OperationDto.OperationStatus.SUCCESS
                            && message.getNewBalance() != null) {

                        WsBalanceEvent balanceEvent = buildBalanceEvent(message);
                        messagingTemplate.convertAndSendToUser(
                                userId.toString(),
                                "/queue/balance/" + message.getOperationId(),
                                balanceEvent
                        );
                    }
                });
    }

    private WsOperationEvent buildOperationEvent(
            OperationResultMessage message) {

        OperationDto operationDto = new OperationDto();
        operationDto.setOperationId(message.getOperationId());
        operationDto.setStatus(message.getStatus());
        operationDto.setFailReason(message.getFailReason());

        WsOperationEvent event = new WsOperationEvent();
        event.setType(message.getStatus() == OperationDto.OperationStatus.SUCCESS
                ? WsOperationEvent.WsEventType.OPERATION_ADDED
                : WsOperationEvent.WsEventType.OPERATION_UPDATED
        );
        event.setOperation(operationDto);

        return event;
    }

    private WsBalanceEvent buildBalanceEvent(
            OperationResultMessage message) {

        WsBalanceEvent event = new WsBalanceEvent();
        event.setType(WsOperationEvent.WsEventType.BALANCE_UPDATED);
        event.setAccountId(message.getOperationId());
        event.setNewBalance(message.getNewBalance());

        return event;
    }
}