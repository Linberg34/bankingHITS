package com.iisovaii.employee_bff.controller;

import com.iisovaii.employee_bff.dto.credit.CreditDetailEmployeeResponse;
import com.iisovaii.employee_bff.dto.credit.CreditListResponse;
import com.iisovaii.employee_bff.dto.credit.CreditPaymentDto;
import com.iisovaii.employee_bff.dto.credit.CreditRatingResponse;
import com.iisovaii.employee_bff.security.CurrentUser;
import com.iisovaii.employee_bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bff/employee")
@RequiredArgsConstructor
public class CreditController {

    private final ProxyService proxyService;

    // кредиты конкретного клиента
    @GetMapping("/clients/{clientId}/credits")
    public ResponseEntity<CreditListResponse> getClientCredits(
            @CurrentUser UUID employeeId,
            @PathVariable UUID clientId) {
        return ResponseEntity.ok(
                proxyService.getClientCredits(clientId)
        );
    }

    // детали кредита
    @GetMapping("/credits/{creditId}")
    public ResponseEntity<CreditDetailEmployeeResponse> getCreditDetail(
            @CurrentUser UUID employeeId,
            @PathVariable UUID creditId) {
        return ResponseEntity.ok(
                proxyService.getCreditDetail(creditId)
        );
    }

    // платежи по кредиту
    @GetMapping("/credits/{creditId}/payments")
    public ResponseEntity<List<CreditPaymentDto>> getPayments(
            @CurrentUser UUID employeeId,
            @PathVariable UUID creditId) {
        return ResponseEntity.ok(
                proxyService.getCreditPayments(creditId)
        );
    }

    // кредитный рейтинг клиента
    @GetMapping("/clients/{clientId}/credits/rating")
    public ResponseEntity<CreditRatingResponse> getCreditRating(
            @CurrentUser UUID employeeId,
            @PathVariable UUID clientId) {
        return ResponseEntity.ok(
                proxyService.getCreditRating(clientId)
        );
    }
}