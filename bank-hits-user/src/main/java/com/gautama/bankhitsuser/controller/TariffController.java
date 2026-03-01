package com.gautama.bankhitsuser.controller;

import com.gautama.bankhitsuser.dto.CreateTariffRequest;
import com.gautama.bankhitsuser.dto.TariffResponse;
import com.gautama.bankhitsuser.model.User;
import com.gautama.bankhitsuser.service.CreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tariffs")
@RequiredArgsConstructor
@Tag(name = "Тарифы", description = "Проксирование запросов в кредитный сервис")
public class TariffController {
    private final CreditService creditService;

    @Operation(summary = "Создать тариф", description = "Только для сотрудника")
    @PostMapping
    public ResponseEntity<TariffResponse> createTariff(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateTariffRequest req) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creditService.createTariff(currentUser.getId(), req));
    }

    @Operation(summary = "Список тарифов")
    @GetMapping
    public ResponseEntity<List<TariffResponse>> getAllTariffs(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(creditService.getAllTariffs(currentUser.getId()));
    }
}
