package com.iisovaii.client_bff.dto.credit;

import java.math.BigDecimal;
import java.util.UUID;

public record RepayCreditResponse(
        UUID creditId,
        BigDecimal remainingDebt,
        CreditStatus status
) {}

