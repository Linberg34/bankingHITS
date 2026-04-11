package com.iisovaii.employee_bff.infrastructure.resilience;

import com.iisovaii.employee_bff.infrastructure.trace.TraceContextHolder;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
@Slf4j
public class DownstreamCallExecutor {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BASE_BACKOFF_MS = 250L;
    private static final int WINDOW_SIZE = 20;
    private static final int MIN_CALLS = 10;
    private static final double FAILURE_THRESHOLD = 70.0;
    private static final Duration OPEN_DURATION = Duration.ofSeconds(20);

    private final RestClient monitoringClient;
    private final Map<DownstreamService, ServiceCircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

    public DownstreamCallExecutor(
            RestClient.Builder restClientBuilder,
            @Value("${monitoring.service-url:http://localhost:8087}") String monitoringServiceUrl
    ) {
        this.monitoringClient = restClientBuilder.baseUrl(monitoringServiceUrl).build();
    }

    public <T> T execute(
            DownstreamService target,
            String method,
            String path,
            int successStatus,
            Supplier<T> action
    ) {
        ServiceCircuitBreaker circuitBreaker = circuitBreakers.computeIfAbsent(target, ignored -> new ServiceCircuitBreaker());
        long startedAt = System.currentTimeMillis();
        int retriesUsed = 0;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            AttemptPermission permission = circuitBreaker.beforeCall();
            if (!permission.allowed()) {
                publish(target, method, path, HttpStatus.SERVICE_UNAVAILABLE.value(), startedAt, retriesUsed, true, circuitBreaker.snapshot());
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Downstream requests to " + target.value + " are temporarily blocked by circuit breaker"
                );
            }

            try {
                T result = action.get();
                circuitBreaker.onSuccess(permission);
                publish(target, method, path, successStatus, startedAt, retriesUsed, false, circuitBreaker.snapshot());
                return result;
            } catch (RuntimeException exception) {
                boolean circuitFailure = isCircuitFailure(exception);
                if (circuitFailure) {
                    circuitBreaker.onFailure(permission);
                } else {
                    circuitBreaker.onSuccess(permission);
                }

                if (!isRetryable(exception) || attempt == MAX_ATTEMPTS) {
                    publish(target, method, path, resolveStatus(exception), startedAt, retriesUsed, false, circuitBreaker.snapshot());
                    throw exception;
                }

                retriesUsed++;
                sleep(attempt);
            }
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected downstream execution state");
    }

    private void publish(
            DownstreamService target,
            String method,
            String path,
            int status,
            long startedAt,
            int retriesUsed,
            boolean blockedByCircuit,
            CircuitSnapshot snapshot
    ) {
        long latencyMs = System.currentTimeMillis() - startedAt;
        MonitoringLevel level = resolveLevel(status, retriesUsed, blockedByCircuit);
        String message = blockedByCircuit
                ? "Запрос заблокирован circuit breaker"
                : status >= 500
                ? "Обнаружена нестабильность upstream-сервиса"
                : "Запрос выполнен";

        MonitoringPayload payload = new MonitoringPayload(
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                TraceContextHolder.appSource(),
                target.value,
                level.value,
                method,
                path,
                status,
                latencyMs,
                retriesUsed,
                blockedByCircuit,
                snapshot.state().name(),
                TraceContextHolder.traceId(),
                message + String.format(" (errorRate=%.1f%%)", snapshot.errorRate())
        );

        log.info(
                "[monitoring] {} {} status={} retries={} blocked={} traceId={} errorRate={}%",
                method,
                path,
                status,
                retriesUsed,
                blockedByCircuit,
                payload.traceId(),
                String.format("%.1f", snapshot.errorRate())
        );

        try {
            monitoringClient.post()
                    .uri("/api/monitoring/logs")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            log.warn("Failed to publish monitoring event for {} {}: {}", method, path, exception.getMessage());
        }
    }

    private MonitoringLevel resolveLevel(int status, int retriesUsed, boolean blockedByCircuit) {
        if (blockedByCircuit || status >= 500) {
            return MonitoringLevel.ERROR;
        }
        if (status >= 400 || retriesUsed > 0) {
            return MonitoringLevel.WARN;
        }
        return MonitoringLevel.INFO;
    }

    private boolean isRetryable(RuntimeException exception) {
        return isCircuitFailure(exception);
    }

    private boolean isCircuitFailure(RuntimeException exception) {
        if (exception instanceof ResponseStatusException) {
            return false;
        }
        if (exception instanceof FeignException feignException) {
            return feignException.status() == -1 || feignException.status() >= 500;
        }
        if (exception instanceof RestClientResponseException responseException) {
            return responseException.getStatusCode().is5xxServerError();
        }
        return exception instanceof RestClientException;
    }

    private int resolveStatus(RuntimeException exception) {
        if (exception instanceof ResponseStatusException responseStatusException) {
            return responseStatusException.getStatusCode().value();
        }
        if (exception instanceof FeignException feignException) {
            return feignException.status() > 0 ? feignException.status() : HttpStatus.SERVICE_UNAVAILABLE.value();
        }
        if (exception instanceof RestClientResponseException responseException) {
            return responseException.getStatusCode().value();
        }
        if (exception instanceof RestClientException) {
            return HttpStatus.SERVICE_UNAVAILABLE.value();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    private void sleep(int attempt) {
        long delayMs = BASE_BACKOFF_MS * (1L << Math.max(0, attempt - 1));
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Interrupted while retrying downstream call", exception);
        }
    }

    public enum DownstreamService {
        USERS("users"),
        CREDITS("credits"),
        CORE("core"),
        SSO("sso");

        private final String value;

        DownstreamService(String value) {
            this.value = value;
        }
    }

    private enum MonitoringLevel {
        INFO("info"),
        WARN("warn"),
        ERROR("error");

        private final String value;

        MonitoringLevel(String value) {
            this.value = value;
        }
    }

    private record MonitoringPayload(
            String id,
            long timestamp,
            String app,
            String service,
            String level,
            String method,
            String path,
            int status,
            long latencyMs,
            int retries,
            boolean blockedByCircuit,
            String circuitState,
            String traceId,
            String message
    ) {
    }

    private record AttemptPermission(boolean allowed, CircuitState state) {
    }

    private record CircuitSnapshot(CircuitState state, double errorRate) {
    }

    private enum CircuitState {
        CLOSED,
        OPEN,
        HALF_OPEN
    }

    private static final class ServiceCircuitBreaker {
        private final Deque<Boolean> outcomes = new ArrayDeque<>();
        private CircuitState state = CircuitState.CLOSED;
        private long openUntilEpochMs;
        private int halfOpenInFlight;

        synchronized AttemptPermission beforeCall() {
            long now = System.currentTimeMillis();
            if (state == CircuitState.OPEN) {
                if (now < openUntilEpochMs) {
                    return new AttemptPermission(false, state);
                }
                state = CircuitState.HALF_OPEN;
                halfOpenInFlight = 0;
            }

            if (state == CircuitState.HALF_OPEN) {
                if (halfOpenInFlight > 0) {
                    return new AttemptPermission(false, state);
                }
                halfOpenInFlight++;
                return new AttemptPermission(true, state);
            }

            return new AttemptPermission(true, state);
        }

        synchronized void onSuccess(AttemptPermission permission) {
            if (permission.state() == CircuitState.HALF_OPEN) {
                halfOpenInFlight = Math.max(0, halfOpenInFlight - 1);
                state = CircuitState.CLOSED;
                outcomes.clear();
                return;
            }

            recordOutcome(true);
        }

        synchronized void onFailure(AttemptPermission permission) {
            if (permission.state() == CircuitState.HALF_OPEN) {
                halfOpenInFlight = Math.max(0, halfOpenInFlight - 1);
                open();
                return;
            }

            recordOutcome(false);
            if (outcomes.size() >= MIN_CALLS && errorRate() > FAILURE_THRESHOLD) {
                open();
            }
        }

        synchronized CircuitSnapshot snapshot() {
            return new CircuitSnapshot(state, errorRate());
        }

        private void recordOutcome(boolean success) {
            outcomes.addLast(success);
            while (outcomes.size() > WINDOW_SIZE) {
                outcomes.removeFirst();
            }
        }

        private void open() {
            state = CircuitState.OPEN;
            openUntilEpochMs = System.currentTimeMillis() + OPEN_DURATION.toMillis();
            halfOpenInFlight = 0;
        }

        private double errorRate() {
            if (outcomes.isEmpty()) {
                return 0.0;
            }

            long failures = outcomes.stream().filter(result -> !result).count();
            return (failures * 100.0) / outcomes.size();
        }
    }
}
