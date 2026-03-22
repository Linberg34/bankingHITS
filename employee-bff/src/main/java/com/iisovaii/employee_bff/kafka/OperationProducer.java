package com.iisovaii.employee_bff.kafka;

import com.iisovaii.employee_bff.dto.kafka.OperationMessage;
import com.iisovaii.employee_bff.dto.operation.DepositRequest;
import com.iisovaii.employee_bff.dto.operation.OperationDto;
import com.iisovaii.employee_bff.dto.operation.TransferRequest;
import com.iisovaii.employee_bff.dto.operation.WithdrawRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class OperationProducer {

    private static final String TOPIC = "account.operations";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendDeposit(
            UUID operationId,
            DepositRequest request,
            UUID initiatedByUserId) {

        OperationMessage message = new OperationMessage(
                operationId,
                OperationDto.OperationType.DEPOSIT,
                request.getAccountId(),
                request.getAmount(),
                null,             // currency определяет AccountService по счёту
                null,             // targetAccountId — только для transfer
                initiatedByUserId,
                Instant.now()
        );

        send(operationId.toString(), message);
    }

    public void sendWithdraw(
            UUID operationId,
            WithdrawRequest request,
            UUID initiatedByUserId) {

        OperationMessage message = new OperationMessage(
                operationId,
                OperationDto.OperationType.WITHDRAW,
                request.getAccountId(),
                request.getAmount(),
                null,
                null,
                initiatedByUserId,
                Instant.now()
        );

        send(operationId.toString(), message);
    }

    public void sendTransfer(
            UUID operationId,
            TransferRequest request,
            UUID initiatedByUserId) {

        OperationMessage message = new OperationMessage(
                operationId,
                OperationDto.OperationType.TRANSFER_OUT,
                request.getFromAccountNumber(),
                request.getAmount(),
                null,
                request.getToAccountNumber(),
                initiatedByUserId,
                Instant.now()
        );

        send(operationId.toString(), message);
    }

    private void send(String key, OperationMessage message) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(TOPIC, key, message);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error(
                        "Не удалось отправить операцию {} в Kafka: {}",
                        message.getOperationId(),
                        ex.getMessage()
                );
            } else {
                log.debug(
                        "Операция {} отправлена в Kafka, offset: {}",
                        message.getOperationId(),
                        result.getRecordMetadata().offset()
                );
            }
        });
    }
}