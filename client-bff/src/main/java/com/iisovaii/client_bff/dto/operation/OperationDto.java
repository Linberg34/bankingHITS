package com.iisovaii.client_bff.dto.operation;

import com.iisovaii.client_bff.dto.common.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OperationDto(
        UUID operationId,
        OperationType type,
        BigDecimal amount,
        Currency currency,
        UUID relatedAccountId,
        String relatedAccountOwner,
        OperationStatus status,
        String failReason,
        LocalDateTime createdAt
) {}

