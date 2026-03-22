package com.iisovaii.client_bff.dto.account;

import java.util.List;

public record AccountListResponse(
        List<AccountDto> accounts
) {}

