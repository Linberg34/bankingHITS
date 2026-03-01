package com.gautama.bankhitsuser.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateTariffRequest {
    @NotBlank(message = "Название тарифа обязательно")
    private String name;

    @NotNull
    @DecimalMin(value = "0.01", message = "Ставка должна быть больше 0")
    @DecimalMax(value = "999.99", message = "Ставка слишком большая")
    private BigDecimal annualRate;
}

