package com.bank.monitoringservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MonitoringTargetService {
    USERS("users"),
    CREDITS("credits"),
    CORE("core"),
    SSO("sso"),
    CLIENT_BFF("client-bff"),
    EMPLOYEE_BFF("employee-bff");

    private final String value;

    MonitoringTargetService(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static MonitoringTargetService fromValue(String value) {
        for (MonitoringTargetService service : values()) {
            if (service.value.equalsIgnoreCase(value)) {
                return service;
            }
        }
        throw new IllegalArgumentException("Unknown monitoring service: " + value);
    }
}
