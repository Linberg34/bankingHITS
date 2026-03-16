package com.iisovaii.employee_bff.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private UUID accountId;
    private Currency currency;
    private BigDecimal balance;
    private AccountStatus status;
    private LocalDateTime createdAt;

    public enum Currency {
        RUB, USD, EUR
    }

    public enum AccountStatus {
        ACTIVE, CLOSED
    }
}