package com.iisovaii.employee_bff.dto.tariff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffDto {
    private UUID tariffId;
    private String name;
    private BigDecimal interestRate;
    private int termDays;
}