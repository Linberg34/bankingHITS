package com.iisovaii.client_bff.dto.operation;

import java.util.UUID;

public record OperationAcceptedResponse(
        UUID operationId,
        OperationStatus status
) {}

