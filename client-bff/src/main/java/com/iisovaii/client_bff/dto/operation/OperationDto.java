package com.iisovaii.client_bff.dto.operation;

import com.iisovaii.client_bff.dto.common.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OperationDto(
        @JsonProperty("id") UUID operationId,
        @JsonProperty("operationType") OperationType type,
        BigDecimal amount,
        Currency currency,
        String accountNumber,
        OperationStatus status,
        String description,
        LocalDateTime createdAt
) {}

