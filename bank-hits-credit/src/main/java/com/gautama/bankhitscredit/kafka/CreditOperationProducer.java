package com.gautama.bankhitscredit.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditOperationProducer {

    private static final String TOPIC = "operations-commands";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendWithdraw(String accountNumber, BigDecimal amount, UUID userId) {
        send("WITHDRAW", accountNumber, amount, userId);
    }

    public void sendDeposit(String accountNumber, BigDecimal amount, UUID userId) {
        send("DEPOSIT", accountNumber, amount, userId);
    }

    private void send(String type, String accountNumber, BigDecimal amount, UUID userId) {
        UUID operationId = UUID.randomUUID();
        OperationCommandMessage command = new OperationCommandMessage(
                operationId,
                type,
                userId,
                Map.of("accountNumber", accountNumber, "amount", amount)
        );
        kafkaTemplate.send(TOPIC, operationId.toString(), command);
        log.info("Sent {} command operationId={} account={} amount={}", type, operationId, accountNumber, amount);
    }
}
