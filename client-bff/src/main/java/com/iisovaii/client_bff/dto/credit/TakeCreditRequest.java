package com.iisovaii.client_bff.dto.credit;

import java.math.BigDecimal;
import java.util.UUID;

public record TakeCreditRequest(
        UUID accountId,
        UUID tariffId,
        BigDecimal amount
) {}

