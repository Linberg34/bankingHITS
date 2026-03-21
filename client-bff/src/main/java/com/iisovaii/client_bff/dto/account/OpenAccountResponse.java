package com.iisovaii.client_bff.dto.account;

import com.iisovaii.client_bff.dto.common.Currency;

import java.math.BigDecimal;

public record OpenAccountResponse(
        String accountNumber,
        Currency currency,
        BigDecimal balance,
        AccountStatus status
) {}

