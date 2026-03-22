package com.iisovaii.client_bff.dto.account;

import com.iisovaii.client_bff.dto.common.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

// dto/account/AccountDto.java
public record AccountDto(
        UUID clientId,          // ← называем как в AccountService
        String accountNumber,
        Currency currency,
        BigDecimal balance,
        AccountStatus status
) {}

