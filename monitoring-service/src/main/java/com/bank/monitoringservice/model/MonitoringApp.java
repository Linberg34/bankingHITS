package com.bank.monitoringservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MonitoringApp {
    CLIENT("client"),
    EMPLOYEE("employee"),
    SYSTEM("system");

    private final String value;

    MonitoringApp(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static MonitoringApp fromValue(String value) {
        for (MonitoringApp app : values()) {
            if (app.value.equalsIgnoreCase(value)) {
                return app;
            }
        }
        throw new IllegalArgumentException("Unknown monitoring app: " + value);
    }
}
