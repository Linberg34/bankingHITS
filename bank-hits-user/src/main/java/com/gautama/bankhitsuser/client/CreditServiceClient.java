package com.gautama.bankhitsuser.client;

import com.gautama.bankhitsuser.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "credit-service", url = "${credit.service.url}")
public interface CreditServiceClient {
    @PostMapping("/api/tariffs")
    TariffResponse createTariff(@RequestBody CreateTariffRequest req);

    @GetMapping("/api/tariffs")
    List<TariffResponse> getAllTariffs();

    @GetMapping("/api/credits")
    List<CreditResponse> getAllCredits();

    @PostMapping("/api/credits")
    CreditResponse takeCredit(@RequestBody TakeCreditInternalRequest req);

    @GetMapping("/api/credits/client/{clientId}")
    List<CreditResponse> getClientCredits(@PathVariable Long clientId);

    @GetMapping("/api/credits/{id}")
    CreditResponse getCredit(@PathVariable Long id);

    @PostMapping("/api/credits/{id}/repay")
    CreditResponse repayCredit(@PathVariable Long id);

    @PostMapping("/api/credits/{id}/repay/partial")
    CreditResponse repayPartial(@PathVariable Long id, @RequestBody PartialRepayRequest req);
}
