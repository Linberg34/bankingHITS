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
    @GetMapping("/credits")
    List<CreditSummaryResponse> getCreditsByUserId(@RequestParam UUID userId);

    @GetMapping("/credits/{creditId}")
    CreditDetailResponse getCreditDetailForEmployee(@PathVariable UUID creditId);

    @GetMapping("/credits/{creditId}/payments")
    List<CreditPaymentResponse> getCreditPayments(@PathVariable UUID creditId);

    @GetMapping("/credits/rating/{userId}")
    CreditRatingResponse getCreditRating(@PathVariable UUID userId);

    @GetMapping("/tariffs")
    List<TariffResponse> getTariffs();

    @PostMapping("/tariffs")
    TariffResponse createTariff(@RequestBody CreateTariffRequest request);
}