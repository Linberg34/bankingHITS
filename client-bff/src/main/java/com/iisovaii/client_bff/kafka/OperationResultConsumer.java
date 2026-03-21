package com.iisovaii.client_bff.kafka;

import com.iisovaii.client_bff.dto.operation.OperationDto;
import com.iisovaii.client_bff.dto.ws.WsBalanceEvent;
import com.iisovaii.client_bff.dto.ws.WsEventType;
import com.iisovaii.client_bff.dto.ws.WsOperationEvent;
import com.iisovaii.client_bff.ws.OperationsWsController;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OperationResultConsumer {

    private static final String OPERATIONS_RESULTS_TOPIC = "operations-results";

    private final OperationsWsController operationsWsController;

    public OperationResultConsumer(OperationsWsController operationsWsController) {
        this.operationsWsController = operationsWsController;
    }

    @KafkaListener(
            topics = OPERATIONS_RESULTS_TOPIC,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOperationResult(OperationResultMessage message) {
        UUID userId = message.getUserId();
        UUID accountId = message.getAccountId();

        if (userId == null || accountId == null) {
            return;
        }

        // событие по операции (добавление/обновление в истории)
        if (message.getOperationId() != null && message.getType() != null && message.getStatus() != null) {
            OperationDto dto = new OperationDto(
                    message.getOperationId(),
                    message.getType(),
                    message.getAmount(),
                    message.getCurrency(),
                    message.getRelatedAccountId(),
                    message.getRelatedAccountOwner(),
                    message.getStatus(),
                    message.getErrorMessage(),
                    message.getCreatedAt()
            );
            WsOperationEvent opEvent = new WsOperationEvent(
                    WsEventType.OPERATION_UPDATED,
                    dto
            );
            operationsWsController.sendOperationEvent(
                    userId.toString(),
                    accountId,
                    opEvent
            );
        }

        // событие по балансу (если ядро прислало новые данные)
        if (message.getNewBalance() != null && message.getCurrency() != null) {
            WsBalanceEvent event = new WsBalanceEvent(
                    WsEventType.BALANCE_UPDATED,
                    accountId,
                    message.getNewBalance(),
                    message.getCurrency()
            );
            operationsWsController.sendBalanceEvent(
                    userId.toString(),
                    accountId,
                    event
            );
        }
    }
}

