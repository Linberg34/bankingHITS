package com.gautama.bankhitscredit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private UUID clientId;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private String status;
}
