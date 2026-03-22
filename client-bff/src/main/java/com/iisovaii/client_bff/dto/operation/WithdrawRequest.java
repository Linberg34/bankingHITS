package com.iisovaii.client_bff.dto.operation;

import java.math.BigDecimal;

public record WithdrawRequest(
        String accountNumber,
        BigDecimal amount
) {}

