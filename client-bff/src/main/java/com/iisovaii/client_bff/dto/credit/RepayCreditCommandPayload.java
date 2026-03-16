package com.iisovaii.client_bff.dto.credit;

import java.math.BigDecimal;
import java.util.UUID;

public record RepayCreditCommandPayload(
        UUID creditId,
        UUID accountId,
        BigDecimal amount
) {}

