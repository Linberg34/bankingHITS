package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.operation.*;
import com.iisovaii.client_bff.kafka.OperationProducer;
import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.ProxyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bff/client")
@RequiredArgsConstructor
public class OperationController {

    private final ProxyService proxyService;
    private final OperationProducer operationProducer;

    @GetMapping("/accounts/{accountId}/operations")
    public ResponseEntity<OperationPageResponse> getOperations(
            @CurrentUser UUID userId,
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // проверяем что счёт принадлежит этому пользователю
        proxyService.checkAccountOwnership(userId, accountId);
        return ResponseEntity.ok(proxyService.getOperations(accountId, page, size));
    }

    @PostMapping("/operations/deposit")
    public ResponseEntity<OperationAcceptedResponse> deposit(
            @CurrentUser UUID userId,
            @RequestBody @Valid DepositRequest request) {
        proxyService.checkAccountOwnership(userId, request.accountId());
        UUID operationId = UUID.randomUUID();
        operationProducer.sendDeposit(operationId, request, userId);
        return ResponseEntity.accepted()
                .body(new OperationAcceptedResponse(operationId, OperationStatus.PENDING));
    }

    @PostMapping("/operations/withdraw")
    public ResponseEntity<OperationAcceptedResponse> withdraw(
            @CurrentUser UUID userId,
            @RequestBody @Valid WithdrawRequest request) {
        proxyService.checkAccountOwnership(userId, request.accountId());
        UUID operationId = UUID.randomUUID();
        operationProducer.sendWithdraw(operationId, request, userId);
        return ResponseEntity.accepted()
                .body(new OperationAcceptedResponse(operationId, OperationStatus.PENDING));
    }

    @PostMapping("/operations/transfer")
    public ResponseEntity<OperationAcceptedResponse> transfer(
            @CurrentUser UUID userId,
            @RequestBody @Valid TransferRequest request) {
        proxyService.checkAccountOwnership(userId, request.fromAccountId());
        UUID operationId = UUID.randomUUID();
        operationProducer.sendTransfer(operationId, request, userId);
        return ResponseEntity.accepted()
                .body(new OperationAcceptedResponse(operationId, OperationStatus.PENDING));
    }
}
