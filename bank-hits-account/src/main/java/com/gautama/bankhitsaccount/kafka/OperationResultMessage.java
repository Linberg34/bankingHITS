package com.gautama.bankhitsaccount.kafka;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OperationResultMessage {
    private UUID operationId;
    private String status;       // SUCCESS, FAILED
    private String errorMessage;

    private UUID userId;
    private UUID accountId;
    private String accountNumber;

    private BigDecimal amount;
    private String currency;
    private String type;         // DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT

    private BigDecimal newBalance;
    private LocalDateTime createdAt;
}
