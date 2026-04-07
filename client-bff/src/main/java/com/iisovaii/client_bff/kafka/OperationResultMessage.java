package com.iisovaii.client_bff.kafka;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class OperationResultMessage {

    private UUID operationId;
    private String status;       // "SUCCESS" | "FAILED"
    private String errorMessage;

    private UUID userId;
    private UUID accountId;
    private String accountNumber;

    private BigDecimal amount;
    private String currency;     // "RUB" | "USD" | "EUR"
    private String type;         // "DEPOSIT" | "WITHDRAW" | "TRANSFER_OUT" etc.

    private BigDecimal newBalance;
    private LocalDateTime createdAt;
}
