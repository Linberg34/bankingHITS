package com.iisovaii.client_bff.dto.ws;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для WS-события операции.
 * Поля не имеют @JsonProperty — имена совпадают с тем, что ожидает фронтенд.
 */
public record WsOperationDto(
        UUID operationId,
        String type,
        BigDecimal amount,
        String currency,
        String accountNumber,
        String status,
        String description,
        LocalDateTime createdAt
) {}
