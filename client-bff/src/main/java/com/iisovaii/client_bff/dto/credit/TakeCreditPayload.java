package com.iisovaii.client_bff.dto.credit;

import java.math.BigDecimal;
import java.util.UUID;

public record TakeCreditPayload(
        UUID clientId,
        String accountNumber,
        UUID tariffId,
        BigDecimal amount
) {}
