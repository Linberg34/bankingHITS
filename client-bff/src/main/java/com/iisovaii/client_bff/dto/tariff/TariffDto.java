package com.iisovaii.client_bff.dto.tariff;

import java.math.BigDecimal;
import java.util.UUID;

public record TariffDto(
        UUID tariffId,
        String name,
        BigDecimal interestRate,
        int termDays
) {}

