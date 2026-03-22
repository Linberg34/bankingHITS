package com.iisovaii.client_bff.dto.ws;

import com.iisovaii.client_bff.dto.common.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record WsBalanceEvent(
        WsEventType type,
        UUID accountId,
        BigDecimal newBalance,
        Currency currency
) {}

