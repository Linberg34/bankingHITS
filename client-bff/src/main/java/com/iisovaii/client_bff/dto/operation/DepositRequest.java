package com.iisovaii.client_bff.dto.operation;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositRequest(
        UUID accountId,
        BigDecimal amount
) {}

