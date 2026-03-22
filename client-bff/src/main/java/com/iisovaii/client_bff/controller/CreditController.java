package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.credit.*;
import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.ProxyService;
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
    @Operation(
            summary = "Список кредитов клиента",
            description = "Возвращает кредиты текущего клиента."
    )
    public ResponseEntity<CreditListResponse> getCredits(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(proxyService.getCredits(userId));
    }

    @GetMapping("/{creditId}")
    @Operation(
            summary = "Детали кредита",
            description = "Возвращает подробную информацию по выбранному кредиту текущего клиента."
    )
    public ResponseEntity<CreditDetailResponse> getCreditDetail(
            @CurrentUser UUID userId,
            @PathVariable UUID creditId) {
        // ProxyService проверит что кредит принадлежит этому userId
        return ResponseEntity.ok(proxyService.getCreditDetail(userId, creditId));
    }

    @GetMapping("/{creditId}/payments")
    @Operation(
            summary = "Платежи по кредиту",
            description = "Возвращает график и фактические платежи по кредиту (включая просрочки)."
    )
    public ResponseEntity<List<CreditPaymentDto>> getPayments(
            @CurrentUser UUID userId,
            @PathVariable UUID creditId) {
        proxyService.checkCreditOwnership(userId, creditId);
        return ResponseEntity.ok(proxyService.getCreditPayments(creditId));
    }

    @PostMapping
    @Operation(
            summary = "Взять кредит",
            description = "Выдаёт кредит через CreditService и возвращает информацию о новом кредите."
    )
    public ResponseEntity<TakeCreditResponse> takeCredit(
            @CurrentUser UUID userId,
            @RequestBody @Valid TakeCreditRequest request) {
        proxyService.checkAccountOwnership(userId, request.accountNumber());
        TakeCreditResponse response = proxyService.takeCredit(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{creditId}/repay")
    @Operation(
            summary = "Погасить кредит",
            description = "Погашает кредит через CreditService и возвращает остаток долга."
    )
    public ResponseEntity<RepayCreditResponse> repayCredit(
            @CurrentUser UUID userId,
            @PathVariable UUID creditId,
            @RequestBody @Valid RepayCreditRequest request) {
        proxyService.checkCreditOwnership(userId, creditId);
        RepayCreditResponse response = proxyService.repayCredit(userId, creditId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rating")
    @Operation(
            summary = "Кредитный рейтинг",
            description = "Возвращает кредитный рейтинг текущего клиента."
    )
    public ResponseEntity<CreditRatingResponse> getRating(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(proxyService.getCreditRating(userId));
    }
}
