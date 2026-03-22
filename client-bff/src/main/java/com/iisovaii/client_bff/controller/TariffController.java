package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.tariff.TariffDto;
import com.iisovaii.client_bff.service.ProxyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// controller/TariffController.java
@RestController
@RequestMapping("/bff/client/tariffs")
@RequiredArgsConstructor
@Tag(name = "Tariffs", description = "Просмотр кредитных тарифов")
public class TariffController {

    private final ProxyService proxyService;

    @GetMapping
    @Operation(
            summary = "Список кредитных тарифов",
            description = "Возвращает доступные кредитные тарифы для клиентов банка."
    )
    public ResponseEntity<List<TariffDto>> getTariffs() {
        return ResponseEntity.ok(proxyService.getTariffs());
    }
}
