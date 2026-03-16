package com.iisovaii.client_bff.kafka;

import com.iisovaii.client_bff.dto.common.Currency;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class OperationResultMessage {

    private UUID operationId;
    private String status;
    private String errorCode;
    private String errorMessage;
    private UUID userId;
    private UUID accountId;
    private BigDecimal newBalance;
    private Currency currency;

    public OperationResultMessage() {
    }

    public OperationResultMessage(UUID operationId,
                                  String status,
                                  String errorCode,
                                  String errorMessage,
                                  UUID userId,
                                  UUID accountId,
                                  BigDecimal newBalance,
                                  Currency currency) {
        this.operationId = operationId;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.userId = userId;
        this.accountId = accountId;
        this.newBalance = newBalance;
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationResultMessage that = (OperationResultMessage) o;
        return Objects.equals(operationId, that.operationId)
                && Objects.equals(status, that.status)
                && Objects.equals(errorCode, that.errorCode)
                && Objects.equals(errorMessage, that.errorMessage)
                && Objects.equals(userId, that.userId)
                && Objects.equals(accountId, that.accountId)
                && Objects.equals(newBalance, that.newBalance)
                && currency == that.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId, status, errorCode, errorMessage, userId, accountId, newBalance, currency);
    }

    @Override
    public String toString() {
        return "OperationResultMessage{" +
                "operationId=" + operationId +
                ", status='" + status + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", userId=" + userId +
                ", accountId=" + accountId +
                ", newBalance=" + newBalance +
                ", currency=" + currency +
                '}';
    }
}

