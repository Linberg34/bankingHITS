package com.iisovaii.employee_bff.dto.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationDto {
    private UUID operationId;
    private OperationType type;
    private BigDecimal amount;
    private Currency currency;
    private UUID relatedAccountId;
    private String relatedAccountOwner;
    private OperationStatus status;
    private String failReason;
    private LocalDateTime createdAt;

    public enum OperationType {
        DEPOSIT, WITHDRAW,
        TRANSFER_IN, TRANSFER_OUT,
        CREDIT_ISSUE, CREDIT_PAYMENT
    }

    public enum Currency {
        RUB, USD, EUR
    }

    public enum OperationStatus {
        PENDING, SUCCESS, FAILED
    }
}
