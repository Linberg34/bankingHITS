package com.iisovaii.client_bff.client;

import com.iisovaii.client_bff.config.FeignConfig;
import com.iisovaii.client_bff.dto.credit.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "credit-service",
        url = "${services.credit-service-url}",
        configuration = FeignConfig.class
)
public interface CreditServiceClient {
    @GetMapping("/internal/credits")
    CreditListResponse getCredits(@RequestParam("userId") UUID userId);

    @GetMapping("/internal/credits/{creditId}")
    CreditDetailResponse getCreditDetail(
            @PathVariable("creditId") UUID creditId,
            @RequestParam("userId") UUID userId
    );

    @GetMapping("/internal/credits/{creditId}/ownership")
    void checkCreditOwnership(
            @PathVariable("creditId") UUID creditId,
            @RequestParam("userId") UUID userId
    );

    @GetMapping("/internal/credits/{creditId}/payments")
    List<CreditPaymentDto> getCreditPayments(@PathVariable("creditId") UUID creditId);

    @PostMapping("/internal/credits")
    TakeCreditResponse takeCredit(
            @RequestParam("userId") UUID userId,
            @RequestBody TakeCreditRequest request
    );

    @PostMapping("/internal/credits/{creditId}/repay")
    RepayCreditResponse repayCredit(
            @PathVariable("creditId") UUID creditId,
            @RequestParam("userId") UUID userId,
            @RequestBody RepayCreditRequest request
    );

    @GetMapping("/internal/credits/rating")
    CreditRatingResponse getCreditRating(@RequestParam("userId") UUID userId);
}

