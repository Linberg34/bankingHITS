package com.iisovaii.client_bff.dto.account;

public record CloseAccountResponse(
        String accountNumber,
        AccountStatus status
) {}

