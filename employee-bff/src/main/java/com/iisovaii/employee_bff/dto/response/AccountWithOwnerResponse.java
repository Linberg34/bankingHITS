package com.iisovaii.employee_bff.dto.response;

import com.iisovaii.employee_bff.dto.account.AccountDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountWithOwnerResponse {
    private UUID accountId;
    private UUID ownerId;
    private String firstName;      // UserService отдаёт отдельно
    private String lastName;       // склеиваем в маппере в ownerFullName
    private AccountDto.Currency currency;
    private BigDecimal balance;
    private AccountDto.AccountStatus status;
    private LocalDateTime createdAt;
}
