package com.iisovaii.client_bff.dto.credit;

import java.math.BigDecimal;

public record RepayCreditRequest(
        BigDecimal amount
) {}

