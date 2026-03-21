package com.gautama.bankhitscredit.controller;

import com.gautama.bankhitscredit.dto.*;
import com.gautama.bankhitscredit.service.CreditService;
import com.gautama.bankhitscredit.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;
    private final RatingService ratingService;

    @GetMapping("/credits")
    public ResponseEntity<List<CreditResponse>> getAllCredits() {
        return ResponseEntity.ok(creditService.getAllCredits());
    }

    @GetMapping("/credits/{id}")
    public ResponseEntity<CreditResponse> getCredit(
            @PathVariable UUID id) {
        return ResponseEntity.ok(creditService.getCredit(id));
    }

    @GetMapping("/credits/client/{clientId}")
    public ResponseEntity<List<CreditResponse>> getClientCredits(
            @PathVariable UUID clientId) {
        return ResponseEntity.ok(
                creditService.getClientCredits(clientId)
        );
    }

    @PostMapping("/credits")
    public ResponseEntity<CreditResponse> takeCredit(
            @RequestBody @Valid TakeCreditRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creditService.takeCredit(request));
    }

    @PostMapping("/credits/{id}/repay")
    public ResponseEntity<CreditResponse> repayCredit(
            @PathVariable UUID id) {
        return ResponseEntity.ok(creditService.repayFull(id));
    }

    @PostMapping("/credits/{id}/repay/partial")
    public ResponseEntity<CreditResponse> repayPartial(
            @PathVariable UUID id,
            @RequestBody @Valid PartialRepayRequest request) {
        return ResponseEntity.ok(
                creditService.repayPartial(id, request)
        );
    }

    @GetMapping("/credits/{id}/payments")
    public ResponseEntity<List<CreditPaymentResponse>> getPayments(
            @PathVariable UUID id) {
        return ResponseEntity.ok(creditService.getPayments(id));
    }

    @GetMapping("/credits/rating/{clientId}")
    public ResponseEntity<CreditRatingResponse> getRating(
            @PathVariable UUID clientId) {
        return ResponseEntity.ok(
                ratingService.calculateRating(clientId)
        );
    }

    @GetMapping("/tariffs")
    public ResponseEntity<List<TariffResponse>> getAllTariffs() {
        return ResponseEntity.ok(creditService.getAllTariffs());
    }

    @PostMapping("/tariffs")
    public ResponseEntity<TariffResponse> createTariff(
            @RequestBody @Valid CreateTariffRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creditService.createTariff(request));
    }
}