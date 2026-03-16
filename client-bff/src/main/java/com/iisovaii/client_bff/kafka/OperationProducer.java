package com.iisovaii.client_bff.kafka;

import com.iisovaii.client_bff.dto.account.OpenAccountRequest;
import com.iisovaii.client_bff.dto.credit.RepayCreditCommandPayload;
import com.iisovaii.client_bff.dto.credit.RepayCreditRequest;
import com.iisovaii.client_bff.dto.credit.TakeCreditRequest;
import com.iisovaii.client_bff.dto.operation.DepositRequest;
import com.iisovaii.client_bff.dto.operation.TransferRequest;
import com.iisovaii.client_bff.dto.operation.WithdrawRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OperationProducer {

    private static final String OPERATIONS_COMMANDS_TOPIC = "operations-commands";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OperationProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOpenAccount(UUID operationId, OpenAccountRequest request, UUID userId) {
        sendCommand(operationId, "OPEN_ACCOUNT", request, userId);
    }

    public void sendCloseAccount(UUID operationId, UUID accountId, UUID userId) {
        sendCommand(operationId, "CLOSE_ACCOUNT", accountId, userId);
    }

    public void sendDeposit(UUID operationId, DepositRequest request, UUID userId) {
        sendCommand(operationId, "DEPOSIT", request, userId);
    }

    public void sendWithdraw(UUID operationId, WithdrawRequest request, UUID userId) {
        sendCommand(operationId, "WITHDRAW", request, userId);
    }

    public void sendTransfer(UUID operationId, TransferRequest request, UUID userId) {
        sendCommand(operationId, "TRANSFER", request, userId);
    }

    public void sendTakeCredit(UUID operationId, TakeCreditRequest request, UUID userId) {
        sendCommand(operationId, "TAKE_CREDIT", request, userId);
    }

    public void sendRepayCredit(UUID operationId, UUID creditId, RepayCreditRequest request, UUID userId) {
        RepayCreditCommandPayload payload = new RepayCreditCommandPayload(creditId, request.accountId(), request.amount());
        sendCommand(operationId, "REPAY_CREDIT", payload, userId);
    }

    private void sendCommand(UUID operationId, String type, Object payload, UUID userId) {
        OperationCommand command = new OperationCommand(operationId, type, userId, payload);
        kafkaTemplate.send(OPERATIONS_COMMANDS_TOPIC, operationId.toString(), command);
    }
}

