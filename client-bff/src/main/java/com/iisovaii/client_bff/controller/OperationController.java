package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.operation.*;
import com.iisovaii.client_bff.kafka.OperationProducer;
import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.ProxyService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bff/client")
@RequiredArgsConstructor
@Tag(name = "Operations", description = "Операции по счетам")
public class OperationController {

    private final ProxyService proxyService;
    private final OperationProducer operationProducer;

    @GetMapping("/accounts/{accountNumber}/operations")
    @Operation(summary = "История операций по счету")
    public ResponseEntity<OperationPageResponse> getOperations(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @PathVariable("accountNumber") String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        proxyService.checkAccountOwnership(userId, accountNumber);
        return ResponseEntity.ok(
                proxyService.getOperations(accountNumber, page, size)
        );
    }

    @PostMapping("/operations/deposit")
    @Operation(summary = "Пополнить счет")
    public ResponseEntity<OperationAcceptedResponse> deposit(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @RequestBody @Valid DepositRequest request) {
        proxyService.checkAccountOwnership(userId, request.accountNumber());
        UUID operationId = UUID.randomUUID();
        operationProducer.sendDeposit(operationId, request, userId);
        return ResponseEntity.accepted()
                .body(new OperationAcceptedResponse(
                        operationId, OperationStatus.PENDING)
                );
    }

    @PostMapping("/operations/withdraw")
    @Operation(summary = "Снять деньги со счета")
    public ResponseEntity<OperationAcceptedResponse> withdraw(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @RequestBody @Valid WithdrawRequest request) {
        proxyService.checkAccountOwnership(userId, request.accountNumber());
        UUID operationId = UUID.randomUUID();
        operationProducer.sendWithdraw(operationId, request, userId);
        return ResponseEntity.accepted()
                .body(new OperationAcceptedResponse(
                        operationId, OperationStatus.PENDING)
                );
    }

    @PostMapping("/operations/transfer")
    @Operation(summary = "Перевод между счетами")
    public ResponseEntity<OperationAcceptedResponse> transfer(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @RequestBody @Valid TransferRequest request) {
        proxyService.checkAccountOwnership(
                userId, request.fromAccountNumber()
        );
        UUID operationId = UUID.randomUUID();
        operationProducer.sendTransfer(operationId, request, userId);
        return ResponseEntity.accepted()
                .body(new OperationAcceptedResponse(
                        operationId, OperationStatus.PENDING)
                );
    }
}