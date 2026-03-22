package com.iisovaii.client_bff.dto.credit;

import java.util.List;

public record CreditListResponse(
        List<CreditSummaryDto> credits
) {}

