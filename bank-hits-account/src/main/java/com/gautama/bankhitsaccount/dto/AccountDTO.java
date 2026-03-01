package com.gautama.bankhitsaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class AccountDTO {
    private Long clientId;
    private String accountNumber;
    private BigDecimal balance;
    private String status;
}
