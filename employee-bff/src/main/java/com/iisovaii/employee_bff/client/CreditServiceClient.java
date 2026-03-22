package com.iisovaii.employee_bff.client;

import com.iisovaii.employee_bff.config.FeignConfig;
import com.iisovaii.employee_bff.dto.credit.CreditRatingResponse;
import com.iisovaii.employee_bff.dto.response.CreditDetailResponse;
import com.iisovaii.employee_bff.dto.response.CreditPaymentResponse;
import com.iisovaii.employee_bff.dto.response.CreditSummaryResponse;
import com.iisovaii.employee_bff.dto.response.TariffResponse;
import com.iisovaii.employee_bff.dto.tariff.CreateTariffRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// client/CreditServiceClient.java
@FeignClient(
        name = "credit-service",
        url = "${services.credit-service-url}",
        configuration = FeignConfig.class
)
public interface CreditServiceClient {

    @GetMapping("/api/credits/client/{clientId}")
    List<CreditSummaryResponse> getCreditsByUserId(@PathVariable("clientId") UUID clientId);

    @GetMapping("/api/credits/{id}")
    CreditDetailResponse getCreditDetailForEmployee(@PathVariable("id") UUID id);

    @GetMapping("/api/credits/{id}/payments")
    List<CreditPaymentResponse> getCreditPayments(@PathVariable("id") UUID id);

    @GetMapping("/api/credits/rating/{clientId}")
    CreditRatingResponse getCreditRating(@PathVariable("clientId") UUID clientId);

    @GetMapping("/api/tariffs")
    List<TariffResponse> getTariffs();

    @PostMapping("/api/tariffs")
    TariffResponse createTariff(@RequestBody CreateTariffRequest request);
}
