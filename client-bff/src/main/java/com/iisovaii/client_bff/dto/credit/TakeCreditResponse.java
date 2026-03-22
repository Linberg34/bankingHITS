package com.iisovaii.client_bff.dto.credit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TakeCreditResponse(
        @JsonProperty("id") UUID creditId,
        @JsonProperty("principalAmount") BigDecimal amount,
        BigDecimal remainingDebt,
        @JsonProperty("annualRate") BigDecimal interestRate,
        String tariffName,
        CreditStatus status,
        LocalDateTime nextPaymentAt
) {}

