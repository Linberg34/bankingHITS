package com.gautama.bankhitscredit.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TakeCreditRequest {
    @NotNull
    private UUID clientId;

    @NotBlank
    private String accountNumber;

    @NotNull
    private UUID tariffId;

    @NotNull
    @Positive
    private BigDecimal amount;
}