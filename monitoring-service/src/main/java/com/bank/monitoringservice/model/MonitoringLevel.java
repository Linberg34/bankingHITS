package com.bank.monitoringservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MonitoringLevel {
    INFO("info"),
    WARN("warn"),
    ERROR("error");

    private final String value;

    MonitoringLevel(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static MonitoringLevel fromValue(String value) {
        for (MonitoringLevel level : values()) {
            if (level.value.equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown monitoring level: " + value);
    }
}
