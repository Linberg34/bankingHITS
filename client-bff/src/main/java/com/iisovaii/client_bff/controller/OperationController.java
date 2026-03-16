package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.operation.*;
import com.iisovaii.client_bff.kafka.OperationProducer;
import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.ProxyService;
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
@Tag(name = "Operations", description = "История операций и денежные операции по счетам")
public class OperationController {

    private final ProxyService proxyService;
    private final OperationProducer operationProducer;

    @GetMapping("/accounts/{accountId}/operations")
    @Operation(
            summary = "История операций по счету",
            description = "Возвращает страницу операций по указанному счету текущего клиента."
    )
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
    @Operation(
            summary = "Пополнить счет",
            description = "Создаёт команду на пополнение счета через Kafka и возвращает operationId."
    )
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
    @Operation(
            summary = "Снять деньги со счета",
            description = "Создаёт команду на снятие средств через Kafka и возвращает operationId."
    )
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
    @Operation(
            summary = "Перевод между счетами",
            description = "Создаёт команду на перевод средств (между своими или на чужой счет) через Kafka и возвращает operationId."
    )
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
