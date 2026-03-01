package com.gautama.bankhitsuser.controller;

import com.gautama.bankhitsuser.dto.CreditResponse;
import com.gautama.bankhitsuser.dto.PartialRepayRequest;
import com.gautama.bankhitsuser.dto.TakeCreditRequest;
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
@RequestMapping("/api/credits")
@RequiredArgsConstructor
@Tag(name = "Кредиты", description = "Проксирование запросов в кредитный сервис")
public class CreditController {
    private final CreditService creditService;

    @Operation(summary = "Взять кредит")
    @PostMapping
    public ResponseEntity<CreditResponse> takeCredit(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody TakeCreditRequest req) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creditService.takeCredit(currentUser.getId(), req));
    }

    @Operation(summary = "Все кредиты", description = "Только для сотрудника")
    @GetMapping
    public ResponseEntity<List<CreditResponse>> getAllCredits(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(creditService.getAllCredits(currentUser.getId()));
    }

    @Operation(summary = "Кредиты клиента")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<CreditResponse>> getClientCredits(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long clientId) {
        return ResponseEntity.ok(creditService.getClientCredits(currentUser.getId(), clientId));
    }

    @Operation(summary = "Детали кредита")
    @GetMapping("/{id}")
    public ResponseEntity<CreditResponse> getCredit(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        return ResponseEntity.ok(creditService.getCredit(currentUser.getId(), id));
    }

    @Operation(summary = "Погасить кредит полностью")
    @PostMapping("/{id}/repay")
    public ResponseEntity<CreditResponse> repayCredit(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        return ResponseEntity.ok(creditService.repayCredit(currentUser.getId(), id));
    }

    @Operation(summary = "Частичное погашение кредита")
    @PostMapping("/{id}/repay/partial")
    public ResponseEntity<CreditResponse> repayPartial(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody PartialRepayRequest req) {
        return ResponseEntity.ok(creditService.repayPartial(currentUser.getId(), id, req));
    }
}