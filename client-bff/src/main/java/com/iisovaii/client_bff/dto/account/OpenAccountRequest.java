package com.iisovaii.client_bff.dto.account;

import com.iisovaii.client_bff.dto.common.Currency;

public record OpenAccountRequest(
        Currency currency
) {}

