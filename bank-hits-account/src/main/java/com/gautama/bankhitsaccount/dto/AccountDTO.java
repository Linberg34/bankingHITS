package com.gautama.bankhitsaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private UUID clientId;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private String status;
}
