package com.iisovaii.employee_bff.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllAccountsPageResponse {
    private List<AccountWithOwnerDto> content;
    private int page;
    private int size;
    private long totalElements;
}
