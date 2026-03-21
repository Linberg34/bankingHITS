package com.iisovaii.employee_bff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffResponse {
    private UUID tariffId;
    private String name;
    private BigDecimal interestRate;
    private int termDays;
}