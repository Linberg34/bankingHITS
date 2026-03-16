package com.iisovaii.employee_bff.dto.tariff;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTariffRequest {
    @NotBlank
    private String name;

    @NotNull
    @Positive
    private BigDecimal interestRate;

    @NotNull
    @Positive
    private int termDays;
}