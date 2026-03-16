package com.iisovaii.client_bff.client;

import com.iisovaii.client_bff.config.FeignConfig;
import com.iisovaii.client_bff.dto.profile.ClientProfileResponse;
import com.iisovaii.client_bff.dto.tariff.TariffDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "user-service",
        url = "${services.user-service-url}",
        configuration = FeignConfig.class
)
public interface UserServiceClient {
    @GetMapping("/internal/users/profile")
    ClientProfileResponse getClientProfile(@RequestParam("userId") UUID userId);

    @GetMapping("/internal/tariffs")
    List<TariffDto> getTariffs();
}

