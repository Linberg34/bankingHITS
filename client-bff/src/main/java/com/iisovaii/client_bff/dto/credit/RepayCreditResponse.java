package com.iisovaii.client_bff.dto.credit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record RepayCreditResponse(
        @JsonProperty("id") UUID creditId,
        BigDecimal remainingDebt,
        CreditStatus status
) {}

