package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.credit.*;
import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.ProxyService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bff/client/credits")
@RequiredArgsConstructor
@Tag(name = "Credits", description = "Работа с кредитами клиента")
public class CreditController {

    private final ProxyService proxyService;

    @GetMapping
    @Operation(summary = "Список кредитов клиента")
    public ResponseEntity<CreditListResponse> getCredits(
            @Parameter(hidden = true) @CurrentUser UUID userId) {
        return ResponseEntity.ok(proxyService.getCredits(userId));
    }

    @GetMapping("/{creditId}")
    @Operation(summary = "Детали кредита")
    public ResponseEntity<CreditDetailResponse> getCreditDetail(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @PathVariable("creditId") UUID creditId) {
        return ResponseEntity.ok(
                proxyService.getCreditDetail(userId, creditId)
        );
    }

    @GetMapping("/{creditId}/payments")
    @Operation(summary = "Платежи по кредиту")
    public ResponseEntity<List<CreditPaymentDto>> getPayments(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @PathVariable("creditId") UUID creditId) {
        proxyService.checkCreditOwnership(userId, creditId);
        return ResponseEntity.ok(proxyService.getCreditPayments(creditId));
    }

    @PostMapping
    @Operation(summary = "Взять кредит")
    public ResponseEntity<TakeCreditResponse> takeCredit(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @RequestBody @Valid TakeCreditRequest request) {
        proxyService.checkAccountOwnership(userId, request.accountNumber());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(proxyService.takeCredit(userId, request));
    }

    @PostMapping("/{creditId}/repay")
    @Operation(summary = "Погасить кредит")
    public ResponseEntity<RepayCreditResponse> repayCredit(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @PathVariable("creditId") UUID creditId,
            @RequestBody @Valid RepayCreditRequest request) {
        proxyService.checkCreditOwnership(userId, creditId);
        return ResponseEntity.ok(
                proxyService.repayCredit(userId, creditId, request)
        );
    }

    @GetMapping("/rating")
    @Operation(summary = "Кредитный рейтинг")
    public ResponseEntity<CreditRatingResponse> getRating(
            @Parameter(hidden = true) @CurrentUser UUID userId) {
        return ResponseEntity.ok(proxyService.getCreditRating(userId));
    }
}