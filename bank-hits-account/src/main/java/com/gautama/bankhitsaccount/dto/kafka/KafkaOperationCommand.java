package com.gautama.bankhitsaccount.dto.kafka;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class KafkaOperationCommand {
    UUID operationId;
    String type;
    UUID initiatedByUserId;
    String accountNumber;
    String targetAccountNumber;
    BigDecimal amount;
}
