package com.gautama.bankhitscredit.client;

import com.gautama.bankhitscredit.dto.AccountDTO;
import com.gautama.bankhitscredit.dto.CreateOperationRequest;
import com.gautama.bankhitscredit.dto.OperationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "core-service", url = "${core.service.url}")
public interface AccountServiceClient {
    // пополнение счёта — используется при выдаче кредита
    @PostMapping("/internal/operations/deposit")
    OperationResponse deposit(
            @RequestBody CreateOperationRequest request
    );

    // списание со счёта — используется при погашении кредита
    @PostMapping("/internal/operations/withdraw")
    OperationResponse withdraw(
            @RequestBody CreateOperationRequest request
    );

    // получить счёт по номеру — проверяем что счёт существует
    @GetMapping("/internal/accounts/number/{accountNumber}")
    AccountDTO getAccountByNumber(
            @PathVariable String accountNumber
    );
}