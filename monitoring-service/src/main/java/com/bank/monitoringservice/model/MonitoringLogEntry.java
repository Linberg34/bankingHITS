package com.bank.monitoringservice.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MonitoringLogEntry(
        String id,
        @Min(0) long timestamp,
        @NotNull MonitoringApp app,
        @NotNull MonitoringTargetService service,
        @NotNull MonitoringLevel level,
        @NotBlank String method,
        @NotBlank String path,
        int status,
        @Min(0) long latencyMs,
        @Min(0) int retries,
        boolean blockedByCircuit,
        @NotNull CircuitState circuitState,
        @NotBlank String traceId,
        @NotBlank String message
) {
}
