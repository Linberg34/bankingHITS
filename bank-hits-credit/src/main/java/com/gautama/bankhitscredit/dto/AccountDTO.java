package com.gautama.bankhitscredit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long clientId;
    private String accountNumber;
    private BigDecimal balance;
    private String status;
}