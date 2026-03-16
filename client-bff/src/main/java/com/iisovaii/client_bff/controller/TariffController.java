package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.tariff.TariffDto;
import com.iisovaii.client_bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// controller/TariffController.java
@RestController
@RequestMapping("/bff/client/tariffs")
@RequiredArgsConstructor
public class TariffController {

    private final ProxyService proxyService;

    // клиент только смотрит тарифы, не создаёт
    @GetMapping
    public ResponseEntity<List<TariffDto>> getTariffs() {
        return ResponseEntity.ok(proxyService.getTariffs());
    }
}
