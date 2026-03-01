package com.gautama.bankhitscredit.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "core-service", url = "${core-service.url}")
public interface CoreServiceClient {
    @PostMapping("/account/{accountId}/debit")
    void withdraw(@PathVariable Long accountId, @RequestBody Map<String, Object> body);

    default boolean tryWithdraw(Long accountId, BigDecimal amount) {
        try {
            withdraw(accountId, Map.of("amount", amount));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}