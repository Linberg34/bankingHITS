package com.iisovaii.client_bff.dto.tariff;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record TariffDto(
        @JsonProperty("id") UUID tariffId,
        String name,
        @JsonProperty("annualRate") BigDecimal interestRate,
        int termDays
) {}

