package com.iisovaii.client_bff.dto.account;

import com.iisovaii.client_bff.dto.common.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountDto(
        @JsonProperty("clientId") UUID userId,
        String accountNumber,
        Currency currency,
        BigDecimal balance,
        AccountStatus status
) {}

