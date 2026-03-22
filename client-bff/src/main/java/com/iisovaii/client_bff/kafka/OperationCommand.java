package com.iisovaii.client_bff.kafka;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class OperationCommand {

    private UUID operationId;
    private String type;
    private UUID userId;
    private Object payload;

    public OperationCommand(UUID operationId, String type, UUID userId, Object payload) {
        this.operationId = operationId;
        this.type = type;
        this.userId = userId;
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationCommand that = (OperationCommand) o;
        return Objects.equals(operationId, that.operationId)
                && Objects.equals(type, that.type)
                && Objects.equals(userId, that.userId)
                && Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId, type, userId, payload);
    }

    @Override
    public String toString() {
        return "OperationCommand{" +
                "operationId=" + operationId +
                ", type='" + type + '\'' +
                ", userId=" + userId +
                ", payload=" + payload +
                '}';
    }
}

