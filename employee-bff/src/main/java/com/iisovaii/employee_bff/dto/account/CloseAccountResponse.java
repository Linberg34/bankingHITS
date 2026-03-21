package com.iisovaii.employee_bff.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloseAccountResponse {
    private UUID accountId;
    private AccountDto.AccountStatus status;
}