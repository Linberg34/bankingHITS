package com.iisovaii.employee_bff.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountListResponse {
    private List<AccountDto> accounts;
}