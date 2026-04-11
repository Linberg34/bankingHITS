package com.iisovaii.employee_bff.infrastructure.trace;

import java.util.UUID;

public final class TraceContextHolder {

    public static final String TRACE_HEADER = "X-Trace-Id";
    public static final String APP_HEADER = "X-App-Source";
    public static final String IDEMPOTENCY_HEADER = "Idempotency-Key";

    private static final ThreadLocal<TraceContext> CONTEXT = new ThreadLocal<>();

    private TraceContextHolder() {
    }

    public static void set(String traceId, String appSource) {
        CONTEXT.set(new TraceContext(traceId, appSource));
    }

    public static TraceContext get() {
        TraceContext context = CONTEXT.get();
        if (context == null) {
            context = new TraceContext(generateTraceId(), "system");
            CONTEXT.set(context);
        }
        return context;
    }

    public static String traceId() {
        return get().traceId();
    }

    public static String appSource() {
        return get().appSource();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public static String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    public record TraceContext(String traceId, String appSource) {
    }
}
