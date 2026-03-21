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

@FeignClient(
        name = "credit-service",
        url = "${services.credit-service-url}",
        configuration = FeignConfig.class
)
public interface CreditServiceClient {
    @GetMapping("/api/credits/client/{clientId}")
    List<CreditSummaryResponse> getCreditsByUserId(@PathVariable("clientId") UUID clientId);

    @GetMapping("/api/credits/{creditId}")
    CreditDetailResponse getCreditDetailForEmployee(@PathVariable("creditId") UUID creditId);

    @GetMapping("/api/credits/{creditId}/payments")
    List<CreditPaymentResponse> getCreditPayments(@PathVariable("creditId") UUID creditId);

    @GetMapping("/api/credits/rating/{userId}")
    CreditRatingResponse getCreditRating(@PathVariable("userId") UUID userId);

    @GetMapping("/api/tariffs")
    List<TariffResponse> getTariffs();

    @PostMapping("/api/tariffs")
    TariffResponse createTariff(@RequestBody CreateTariffRequest request);
}
