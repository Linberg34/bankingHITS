package com.iisovaii.client_bff.client;

import com.iisovaii.client_bff.config.FeignConfig;
import com.iisovaii.client_bff.dto.credit.*;
import com.iisovaii.client_bff.dto.tariff.TariffDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "credit-service",
        url = "${services.credit-service-url}",
        configuration = FeignConfig.class
)
public interface CreditServiceClient {
    @GetMapping("/api/credits/client/{clientId}")
    java.util.List<CreditSummaryDto> getCredits(@PathVariable("clientId") UUID userId);

    @GetMapping("/api/credits/{creditId}")
    CreditDetailResponse getCreditDetail(
            @PathVariable("creditId") UUID creditId
    );

    @GetMapping("/api/credits/{creditId}/payments")
    List<CreditPaymentDto> getCreditPayments(@PathVariable("creditId") UUID creditId);

    @PostMapping("/api/credits")
    TakeCreditResponse takeCredit(@RequestBody CreditTakeCreditPayload request);

    @PostMapping("/api/credits/{creditId}/repay")
    RepayCreditResponse repayCredit(@PathVariable("creditId") UUID creditId);

    @PostMapping("/api/credits/{creditId}/repay/partial")
    RepayCreditResponse repayCreditPartial(
            @PathVariable("creditId") UUID creditId,
            @RequestBody RepayCreditRequest request
    );

    @GetMapping("/api/credits/rating/{clientId}")
    CreditRatingResponse getCreditRating(@PathVariable("clientId") UUID userId);

    @GetMapping("/api/tariffs")
    List<TariffDto> getTariffs();
}

