package com.gautama.bankhitscredit.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TakeCreditRequest {
    @NotNull
    private UUID clientId;

    @NotNull
    private UUID accountId;

    @NotNull
    private UUID tariffId;

    @NotNull
    @DecimalMin(value = "1.00", message = "Сумма кредита должна быть больше 0")
    private BigDecimal amount;
}