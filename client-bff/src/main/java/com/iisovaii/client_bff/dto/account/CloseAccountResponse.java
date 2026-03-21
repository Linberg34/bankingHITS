package com.iisovaii.client_bff.dto.account;

import java.util.UUID;

public record CloseAccountResponse(
        UUID accountId,
        AccountStatus status
) {}

