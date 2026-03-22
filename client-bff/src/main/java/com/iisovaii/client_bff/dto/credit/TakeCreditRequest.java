package com.iisovaii.client_bff.dto.credit;

import java.math.BigDecimal;
import java.util.UUID;

public record TakeCreditRequest(
        String accountNumber,
        UUID tariffId,
        BigDecimal amount
) {}

