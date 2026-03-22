package com.iisovaii.client_bff.dto.operation;

import java.util.List;

public record OperationPageResponse(
        List<OperationDto> content,
        int page,
        int size,
        long totalElements
) {}

