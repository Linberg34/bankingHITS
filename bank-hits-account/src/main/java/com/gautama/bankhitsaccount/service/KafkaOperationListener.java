package com.gautama.bankhitsaccount.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gautama.bankhitsaccount.dto.AccountDTO;
import com.gautama.bankhitsaccount.dto.CreateOperationRequest;
import com.gautama.bankhitsaccount.dto.OperationDTO;
import com.gautama.bankhitsaccount.dto.OperationResponse;
import com.gautama.bankhitsaccount.dto.TransferRequest;
import com.gautama.bankhitsaccount.dto.kafka.ClientOperationResultMessage;
import com.gautama.bankhitsaccount.dto.kafka.EmployeeOperationResultMessage;
import com.gautama.bankhitsaccount.dto.kafka.KafkaOperationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaOperationListener {

    private static final String CLIENT_COMMANDS_TOPIC = "operations-commands";
    private static final String CLIENT_RESULTS_TOPIC = "operations-results";
    private static final String EMPLOYEE_COMMANDS_TOPIC = "account.operations";
    private static final String EMPLOYEE_RESULTS_TOPIC = "account.operations.result";

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OperationService operationService;
    private final AccountService accountService;

    @KafkaListener(
            topics = CLIENT_COMMANDS_TOPIC,
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void handleClientCommand(String payload) {
        handleCommand(payload, CLIENT_RESULTS_TOPIC, CommandSource.CLIENT);
    }

    @KafkaListener(
            topics = EMPLOYEE_COMMANDS_TOPIC,
            containerFactory = "kafkaStringListenerContainerFactory"
    )
    public void handleEmployeeCommand(String payload) {
        handleCommand(payload, EMPLOYEE_RESULTS_TOPIC, CommandSource.EMPLOYEE);
    }

    private void handleCommand(String payload, String resultTopic, CommandSource source) {
        KafkaOperationCommand command = null;
        try {
            command = parseCommand(payload, source);
            OperationResponse response = execute(command);
            publishSuccess(resultTopic, source, command, response);
        } catch (Exception exception) {
            log.error("Failed to process {} Kafka command: {}", source, payload, exception);
            publishFailure(resultTopic, source, command, exception);
        }
    }

    private KafkaOperationCommand parseCommand(String payload, CommandSource source) throws Exception {
        JsonNode root = objectMapper.readTree(payload);

        if (source == CommandSource.CLIENT) {
            JsonNode messagePayload = root.path("payload");
            String type = root.path("type").asText();

            return KafkaOperationCommand.builder()
                    .operationId(readUuid(root, "operationId"))
                    .type(type)
                    .initiatedByUserId(readUuid(root, "userId"))
                    .accountNumber(readText(messagePayload, "accountNumber", "fromAccountNumber"))
                    .targetAccountNumber(readText(messagePayload, "toAccountNumber"))
                    .amount(readBigDecimal(messagePayload, "amount"))
                    .build();
        }

        return KafkaOperationCommand.builder()
                .operationId(readUuid(root, "operationId"))
                .type(root.path("type").asText())
                .initiatedByUserId(readUuid(root, "initiatedByUserId"))
                .accountNumber(readText(root, "accountId"))
                .targetAccountNumber(readText(root, "targetAccountId"))
                .amount(readBigDecimal(root, "amount"))
                .build();
    }

    private OperationResponse execute(KafkaOperationCommand command) {
        String normalizedType = normalizeType(command.getType());

        return switch (normalizedType) {
            case "DEPOSIT" -> operationService.deposit(
                    CreateOperationRequest.builder()
                            .accountNumber(command.getAccountNumber())
                            .operationType(normalizedType)
                            .amount(command.getAmount())
                            .description("Kafka deposit")
                            .build()
            );
            case "WITHDRAW" -> operationService.withdraw(
                    CreateOperationRequest.builder()
                            .accountNumber(command.getAccountNumber())
                            .operationType(normalizedType)
                            .amount(command.getAmount())
                            .description("Kafka withdraw")
                            .build()
            );
            case "TRANSFER" -> operationService.transfer(
                    TransferRequest.builder()
                            .fromAccountNumber(command.getAccountNumber())
                            .toAccountNumber(command.getTargetAccountNumber())
                            .amount(command.getAmount())
                            .description("Kafka transfer")
                            .build()
            );
            default -> throw new IllegalArgumentException("Unsupported operation type: " + command.getType());
        };
    }

    private void publishSuccess(
            String resultTopic,
            CommandSource source,
            KafkaOperationCommand command,
            OperationResponse response
    ) {
        if (source == CommandSource.CLIENT) {
            kafkaTemplate.send(
                    resultTopic,
                    command.getOperationId().toString(),
                    buildClientSuccess(command, response)
            );
            return;
        }

        kafkaTemplate.send(
                resultTopic,
                command.getOperationId().toString(),
                buildEmployeeSuccess(command, response)
        );
    }

    private void publishFailure(
            String resultTopic,
            CommandSource source,
            KafkaOperationCommand command,
            Exception exception
    ) {
        String key = command != null && command.getOperationId() != null
                ? command.getOperationId().toString()
                : UUID.randomUUID().toString();

        if (source == CommandSource.CLIENT) {
            kafkaTemplate.send(
                    resultTopic,
                    key,
                    ClientOperationResultMessage.builder()
                            .operationId(command != null ? command.getOperationId() : null)
                            .status("FAILED")
                            .errorCode("OPERATION_FAILED")
                            .errorMessage(exception.getMessage())
                            .userId(command != null ? command.getInitiatedByUserId() : null)
                            .accountId(resolveAccountIdSafely(command != null ? command.getAccountNumber() : null))
                            .amount(command != null ? command.getAmount() : null)
                            .type(resolveClientEventType(command != null ? command.getType() : null))
                            .createdAt(LocalDateTime.now())
                            .build()
            );
            return;
        }

        kafkaTemplate.send(
                resultTopic,
                key,
                EmployeeOperationResultMessage.builder()
                        .correlationId(command != null ? command.getOperationId() : null)
                        .status("FAILED")
                        .failReason(exception.getMessage())
                        .processedAt(Instant.now())
                        .build()
        );
    }

    private ClientOperationResultMessage buildClientSuccess(
            KafkaOperationCommand command,
            OperationResponse response
    ) {
        OperationDTO operation = response.getOperation();
        AccountDTO account = response.getAccount();

        return ClientOperationResultMessage.builder()
                .operationId(operation != null ? operation.getId() : command.getOperationId())
                .status("SUCCESS")
                .userId(command.getInitiatedByUserId())
                .accountId(account != null ? account.getId() : resolveAccountIdSafely(command.getAccountNumber()))
                .amount(operation != null ? operation.getAmount() : command.getAmount())
                .currency(operation != null ? operation.getCurrency() : null)
                .type(operation != null
                        ? resolveClientEventType(operation.getOperationType())
                        : resolveClientEventType(command.getType()))
                .createdAt(operation != null ? operation.getCreatedAt() : LocalDateTime.now())
                .newBalance(account != null ? account.getBalance() : null)
                .relatedAccountId(resolveAccountIdSafely(command.getTargetAccountNumber()))
                .build();
    }

    private EmployeeOperationResultMessage buildEmployeeSuccess(
            KafkaOperationCommand command,
            OperationResponse response
    ) {
        return EmployeeOperationResultMessage.builder()
                .correlationId(command.getOperationId())
                .status("SUCCESS")
                .operationId(response.getOperation() != null ? response.getOperation().getId() : command.getOperationId())
                .newBalance(response.getAccount() != null ? response.getAccount().getBalance() : null)
                .processedAt(Instant.now())
                .build();
    }

    private UUID readUuid(JsonNode root, String fieldName) {
        String value = readText(root, fieldName);
        return value != null ? UUID.fromString(value) : null;
    }

    private BigDecimal readBigDecimal(JsonNode root, String fieldName) {
        JsonNode node = root.path(fieldName);
        if (node.isMissingNode() || node.isNull()) {
            return null;
        }
        return node.decimalValue();
    }

    private String readText(JsonNode root, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode node = root.path(fieldName);
            if (!node.isMissingNode() && !node.isNull()) {
                String value = node.asText();
                if (value != null && !value.isBlank()) {
                    return value;
                }
            }
        }
        return null;
    }

    private UUID resolveAccountIdSafely(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            return null;
        }
        try {
            return accountService.getAccountByNumber(accountNumber).getId();
        } catch (Exception exception) {
            log.warn("Could not resolve accountId for accountNumber {}: {}", accountNumber, exception.getMessage());
            return null;
        }
    }

    private String normalizeType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Operation type is required");
        }

        return switch (type.trim().toUpperCase(Locale.ROOT)) {
            case "DEPOSIT" -> "DEPOSIT";
            case "WITHDRAW", "WITHDRAWAL" -> "WITHDRAW";
            case "TRANSFER", "TRANSFER_OUT" -> "TRANSFER";
            default -> type.trim().toUpperCase(Locale.ROOT);
        };
    }

    private String resolveClientEventType(String type) {
        String normalizedType = normalizeType(type);
        return switch (normalizedType) {
            case "WITHDRAW" -> "WITHDRAW";
            case "TRANSFER" -> "TRANSFER_OUT";
            default -> normalizedType;
        };
    }

    private enum CommandSource {
        CLIENT,
        EMPLOYEE
    }
}
