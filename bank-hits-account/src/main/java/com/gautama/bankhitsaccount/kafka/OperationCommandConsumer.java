package com.gautama.bankhitsaccount.kafka;

import com.gautama.bankhitsaccount.dto.AccountDTO;
import com.gautama.bankhitsaccount.dto.CreateOperationRequest;
import com.gautama.bankhitsaccount.dto.OperationDTO;
import com.gautama.bankhitsaccount.dto.OperationResponse;
import com.gautama.bankhitsaccount.dto.TransferRequest;
import com.gautama.bankhitsaccount.service.OperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OperationCommandConsumer {

    private static final String RESULTS_TOPIC = "operations-results";

    private final OperationService operationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = "operations-commands",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleCommand(OperationCommandMessage command) {
        log.info("Received operation command: id={}, type={}, userId={}",
                command.getOperationId(), command.getType(), command.getUserId());

        try {
            switch (command.getType()) {
                case "DEPOSIT" -> processDeposit(command);
                case "WITHDRAW" -> processWithdraw(command);
                case "TRANSFER" -> processTransfer(command);
                default -> log.warn("Unknown operation type: {}", command.getType());
            }
        } catch (Exception e) {
            log.error("Failed to process command id={}: {}", command.getOperationId(), e.getMessage());
            publishFailure(command.getOperationId(), command.getUserId(), e.getMessage());
        }
    }

    private void processDeposit(OperationCommandMessage command) {
        Map<String, Object> payload = command.getPayload();
        String accountNumber = (String) payload.get("accountNumber");
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());

        CreateOperationRequest request = new CreateOperationRequest();
        request.setAccountNumber(accountNumber);
        request.setAmount(amount);

        OperationResponse response = operationService.deposit(request);
        publishSuccess(command.getOperationId(), command.getUserId(), response, "DEPOSIT");
    }

    private void processWithdraw(OperationCommandMessage command) {
        Map<String, Object> payload = command.getPayload();
        String accountNumber = (String) payload.get("accountNumber");
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());

        CreateOperationRequest request = new CreateOperationRequest();
        request.setAccountNumber(accountNumber);
        request.setAmount(amount);

        OperationResponse response = operationService.withdraw(request);
        publishSuccess(command.getOperationId(), command.getUserId(), response, "WITHDRAW");
    }

    private void processTransfer(OperationCommandMessage command) {
        Map<String, Object> payload = command.getPayload();
        String fromAccountNumber = (String) payload.get("fromAccountNumber");
        String toAccountNumber = (String) payload.get("toAccountNumber");
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber(fromAccountNumber);
        request.setToAccountNumber(toAccountNumber);
        request.setAmount(amount);

        OperationResponse response = operationService.transfer(request);
        publishSuccess(command.getOperationId(), command.getUserId(), response, "TRANSFER_OUT");
    }

    private void publishSuccess(UUID operationId, UUID userId, OperationResponse response, String type) {
        OperationDTO op = response.getOperation();
        AccountDTO account = response.getAccount();

        OperationResultMessage result = OperationResultMessage.builder()
                .operationId(operationId)
                .status("SUCCESS")
                .userId(userId)
                .accountId(account != null ? account.getId() : null)
                .accountNumber(op.getAccountNumber())
                .amount(op.getAmount())
                .currency(op.getCurrency())
                .type(type)
                .newBalance(account != null ? account.getBalance() : null)
                .createdAt(op.getCreatedAt() != null ? op.getCreatedAt() : LocalDateTime.now())
                .build();

        kafkaTemplate.send(RESULTS_TOPIC, operationId.toString(), result);
        log.info("Published success result for operationId={}", operationId);
    }

    private void publishFailure(UUID operationId, UUID userId, String errorMessage) {
        OperationResultMessage result = OperationResultMessage.builder()
                .operationId(operationId)
                .status("FAILED")
                .userId(userId)
                .errorMessage(errorMessage)
                .createdAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send(RESULTS_TOPIC, operationId.toString(), result);
    }
}
