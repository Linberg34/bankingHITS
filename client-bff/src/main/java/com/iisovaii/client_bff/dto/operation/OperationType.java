package com.iisovaii.client_bff.dto.operation;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OperationType {
    DEPOSIT,
    WITHDRAW,
    TRANSFER_IN,
    TRANSFER_OUT,
    CREDIT_ISSUE,
    CREDIT_PAYMENT;

    @JsonCreator
    public static OperationType fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        if ("WITHDRAWAL".equalsIgnoreCase(value)) {
            return WITHDRAW;
        }

        return OperationType.valueOf(value.toUpperCase());
    }
}

