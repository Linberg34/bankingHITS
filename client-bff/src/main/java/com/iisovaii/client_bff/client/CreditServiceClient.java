package com.iisovaii.client_bff.client;

import com.iisovaii.client_bff.config.FeignConfig;
import com.iisovaii.client_bff.dto.credit.*;
import com.iisovaii.client_bff.dto.tariff.TariffResponse;
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
    List<CreditResponse> getCredits(
            @PathVariable("clientId") UUID clientId
    );

    @GetMapping("/api/credits/{id}")
    CreditResponse getCreditDetail(
            @PathVariable("id") UUID id
    );

    @GetMapping("/api/credits/{id}/payments")
    List<CreditPaymentResponse> getCreditPayments(
            @PathVariable("id") UUID id
    );

    @PostMapping("/api/credits")
    CreditResponse takeCredit(
            @RequestBody TakeCreditPayload request
    );

    @PostMapping("/api/credits/{id}/repay")
    CreditResponse repayCredit(
            @PathVariable("id") UUID id
    );

    @PostMapping("/api/credits/{id}/repay/partial")
    CreditResponse repayCreditPartial(
            @PathVariable("id") UUID id,
            @RequestBody PartialRepayPayload request
    );

    @GetMapping("/api/credits/rating/{clientId}")
    CreditRatingResponse getCreditRating(
            @PathVariable("clientId") UUID clientId
    );

    @GetMapping("/api/tariffs")
    List<TariffResponse> getTariffs();
}
