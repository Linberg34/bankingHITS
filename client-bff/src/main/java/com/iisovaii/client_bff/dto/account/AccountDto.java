package com.iisovaii.client_bff.dto.account;

import com.iisovaii.client_bff.dto.common.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountDto(
        UUID accountId,
        Currency currency,
        BigDecimal balance,
        AccountStatus status,
        LocalDateTime createdAt
) {}

