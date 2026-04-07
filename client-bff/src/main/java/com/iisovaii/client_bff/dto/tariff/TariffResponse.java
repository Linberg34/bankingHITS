package com.iisovaii.client_bff.dto.tariff;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TariffResponse(
        UUID id,
        String name,
        BigDecimal annualRate,
        int termDays,
        LocalDateTime createdAt
) {}
