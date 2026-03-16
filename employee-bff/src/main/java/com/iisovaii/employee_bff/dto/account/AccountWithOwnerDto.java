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
public class AccountWithOwnerDto {
    private UUID accountId;
    private UUID ownerId;
    private String ownerFullName;
    private AccountDto.Currency currency;
    private BigDecimal balance;
    private AccountDto.AccountStatus status;
    private LocalDateTime createdAt;
}
