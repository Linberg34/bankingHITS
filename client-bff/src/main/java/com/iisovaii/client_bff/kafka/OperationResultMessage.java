package com.iisovaii.client_bff.kafka;

import com.iisovaii.client_bff.dto.common.Currency;
import com.iisovaii.client_bff.dto.operation.OperationStatus;
import com.iisovaii.client_bff.dto.operation.OperationType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class OperationResultMessage {

    private UUID operationId;
    private OperationStatus status;
    private String errorCode;
    private String errorMessage;

    private UUID userId;
    private UUID accountId;

    private BigDecimal amount;
    private Currency currency;
    private OperationType type;
    private UUID relatedAccountId;
    private String relatedAccountOwner;
    private LocalDateTime createdAt;

    private BigDecimal newBalance;
}

