package com.iisovaii.employee_bff.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationResultMessage {
    private UUID operationId;
    private String status;          // "SUCCESS" | "FAILED"
    private String errorMessage;    // nullable
    private UUID userId;
    private UUID accountId;
    private BigDecimal amount;
    private String currency;        // "RUB" | "USD" | "EUR"
    private String type;            // "DEPOSIT" | "WITHDRAW" | "TRANSFER_OUT" etc.
    private BigDecimal newBalance;
    private LocalDateTime createdAt;
}
