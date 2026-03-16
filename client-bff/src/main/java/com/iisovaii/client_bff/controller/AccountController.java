package com.iisovaii.client_bff.controller;

import com.iisovaii.client_bff.dto.account.AccountListResponse;
import com.iisovaii.client_bff.dto.account.OpenAccountRequest;
import com.iisovaii.client_bff.dto.operation.OperationAcceptedResponse;
import com.iisovaii.client_bff.dto.operation.OperationStatus;
import com.iisovaii.client_bff.kafka.OperationProducer;
import com.iisovaii.client_bff.security.CurrentUser;
import com.iisovaii.client_bff.service.ProxyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bff/client/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Работа со счетами клиента")
public class AccountController {

    private final ProxyService proxyService;
    private final OperationProducer operationProducer;

    @GetMapping
    @Operation(
            summary = "Список счетов клиента",
            description = "Возвращает все счета текущего клиента с балансами и статусами."
    )
    public ResponseEntity<AccountListResponse> getAccounts(
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(proxyService.getAccounts(userId));
    }

    @PostMapping
    @Operation(
            summary = "Открыть новый счет",
            description = "Создаёт запрос на открытие счета через очередь Kafka и возвращает operationId."
    )
    public ResponseEntity<OperationAcceptedResponse> openAccount(
            @CurrentUser UUID userId,
            @RequestBody @Valid OpenAccountRequest request) {
        UUID operationId = UUID.randomUUID();
        operationProducer.sendOpenAccount(operationId, request, userId);
        return ResponseEntity.accepted()
                .body(new OperationAcceptedResponse(operationId, OperationStatus.PENDING));
    }

    @DeleteMapping("/{accountId}")
    @Operation(
            summary = "Закрыть счет",
            description = "Создаёт запрос на закрытие счета через очередь Kafka и возвращает operationId."
    )
    public ResponseEntity<OperationAcceptedResponse> closeAccount(
            @CurrentUser UUID userId,
            @PathVariable UUID accountId) {
        proxyService.checkAccountOwnership(userId, accountId);
        UUID operationId = UUID.randomUUID();
        operationProducer.sendCloseAccount(operationId, accountId, userId);
        return ResponseEntity.accepted()
                .body(new OperationAcceptedResponse(operationId, OperationStatus.PENDING));
    }
}
