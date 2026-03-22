package com.iisovaii.client_bff.dto.operation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OperationDto(
        @JsonProperty("id") UUID operationId,
        @JsonProperty("operationType") String type,
        BigDecimal amount,
        String currency,
        String accountNumber,
        String status,
        String description,
        LocalDateTime createdAt
) {}

