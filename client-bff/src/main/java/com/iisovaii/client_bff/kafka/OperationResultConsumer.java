package com.iisovaii.client_bff.kafka;

import com.iisovaii.client_bff.dto.ws.WsBalanceEvent;
import com.iisovaii.client_bff.dto.ws.WsEventType;
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

