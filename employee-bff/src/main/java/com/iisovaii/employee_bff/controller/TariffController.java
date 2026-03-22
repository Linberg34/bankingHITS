package com.iisovaii.employee_bff.controller;

import com.iisovaii.employee_bff.dto.tariff.CreateTariffRequest;
import com.iisovaii.employee_bff.dto.tariff.CreateTariffResponse;
import com.iisovaii.employee_bff.dto.tariff.TariffDto;
import com.iisovaii.employee_bff.security.CurrentUser;
import com.iisovaii.employee_bff.service.ProxyService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bff/employee/tariffs")
@RequiredArgsConstructor
public class TariffController {

    private final ProxyService proxyService;

    @GetMapping
    public ResponseEntity<List<TariffDto>> getTariffs(
            @Parameter(hidden = true) @CurrentUser UUID employeeId) {
        return ResponseEntity.ok(proxyService.getTariffs());
    }

    // только сотрудник может создавать тарифы
    @PostMapping
    public ResponseEntity<CreateTariffResponse> createTariff(
            @Parameter(hidden = true) @CurrentUser UUID employeeId,
            @RequestBody @Valid CreateTariffRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(proxyService.createTariff(request));
    }
}