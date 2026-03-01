package com.gautama.bankhitscredit.client;

import com.gautama.bankhitscredit.dto.CreateOperationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;


@FeignClient(name = "core-service", url = "${core-service.url}")
public interface CoreServiceClient {
    @PostMapping("/operations/withdraw")
    void withdraw(@RequestBody CreateOperationRequest request);

    default boolean tryWithdraw(String accountNumber, BigDecimal amount) {
        try {
            withdraw(CreateOperationRequest.builder()
                    .accountNumber(accountNumber)
                    .operationType("WITHDRAWAL")
                    .amount(amount)
                    .description("Списание по кредиту")
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}