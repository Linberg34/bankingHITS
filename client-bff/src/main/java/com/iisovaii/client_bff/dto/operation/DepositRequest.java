package com.iisovaii.client_bff.dto.operation;

import java.math.BigDecimal;

public record DepositRequest(
        String accountNumber,
        BigDecimal amount
) {}

