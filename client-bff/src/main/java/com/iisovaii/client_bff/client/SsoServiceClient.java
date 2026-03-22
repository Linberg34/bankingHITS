package com.iisovaii.client_bff.client;

import com.iisovaii.client_bff.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "sso-service",
        url = "${sso.token-url}",
        configuration = FeignConfig.class
)
public interface SsoServiceClient {
    @PostMapping
    Map<String, Object> exchangeCode(@RequestBody MultiValueMap<String, String> body);
}

