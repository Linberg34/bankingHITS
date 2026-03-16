package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.credit.*;
import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.ProxyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bff/client/credits")
@RequiredArgsConstructor
public class CreditController {

    private final ProxyService proxyService;

    @GetMapping
    public ResponseEntity<CreditListResponse> getCredits(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(proxyService.getCredits(userId));
    }

    @GetMapping("/{creditId}")
    public ResponseEntity<CreditDetailResponse> getCreditDetail(
            @CurrentUser UUID userId,
            @PathVariable UUID creditId) {
        // ProxyService проверит что кредит принадлежит этому userId
        return ResponseEntity.ok(proxyService.getCreditDetail(userId, creditId));
    }

    @GetMapping("/{creditId}/payments")
    public ResponseEntity<List<CreditPaymentDto>> getPayments(
            @CurrentUser UUID userId,
            @PathVariable UUID creditId) {
        proxyService.checkCreditOwnership(userId, creditId);
        return ResponseEntity.ok(proxyService.getCreditPayments(creditId));
    }

    @PostMapping
    public ResponseEntity<TakeCreditResponse> takeCredit(
            @CurrentUser UUID userId,
            @RequestBody @Valid TakeCreditRequest request) {
        proxyService.checkAccountOwnership(userId, request.accountId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(proxyService.takeCredit(userId, request));
    }

    @PostMapping("/{creditId}/repay")
    public ResponseEntity<RepayCreditResponse> repayCredit(
            @CurrentUser UUID userId,
            @PathVariable UUID creditId,
            @RequestBody @Valid RepayCreditRequest request) {
        proxyService.checkCreditOwnership(userId, creditId);
        proxyService.checkAccountOwnership(userId, request.accountId());
        return ResponseEntity.ok(proxyService.repayCredit(userId, creditId, request));
    }

    @GetMapping("/rating")
    public ResponseEntity<CreditRatingResponse> getRating(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(proxyService.getCreditRating(userId));
    }
}
