package com.gautama.bankhitscredit.controller;

import com.gautama.bankhitscredit.dto.*;
import com.gautama.bankhitscredit.service.CreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Кредитный сервис", description = "Управление кредитами и тарифами")
public class CreditController {
    private final CreditService creditService;

    @Operation(summary = "Создать тариф", description = "Создаёт новый кредитный тариф. Доступно сотруднику.")
    @PostMapping("/tariffs")
    @ResponseStatus(HttpStatus.CREATED)
    public TariffResponse createTariff(@Valid @RequestBody CreateTariffRequest req) {
        return creditService.createTariff(req);
    }

    @Operation(summary = "Список тарифов", description = "Возвращает все доступные кредитные тарифы.")
    @ApiResponse(responseCode = "200", description = "Список тарифов")
    @GetMapping("/tariffs")
    public List<TariffResponse> getAllTariffs() {
        return creditService.getAllTariffs();
    }


    @Operation(summary = "Все кредиты", description = "Возвращает кредиты всех клиентов. Доступно сотруднику.")
    @ApiResponse(responseCode = "200", description = "Список всех кредитов")
    @GetMapping("/credits")
    public List<CreditResponse> getAllCredits() {
        return creditService.getAllCredits();
    }

    @Operation(summary = "Взять кредит", description = "Оформляет кредит для клиента и зачисляет сумму на указанный счёт.")
    @PostMapping("/credits")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditResponse takeCredit(@Valid @RequestBody TakeCreditRequest req) {
        return creditService.takeCredit(req);
    }

    @Operation(summary = "Кредиты клиента", description = "Возвращает все кредиты конкретного клиента.")
    @GetMapping("/credits/client/{clientId}")
    public List<CreditResponse> getClientCredits(
            @Parameter(description = "ID клиента") @PathVariable Long clientId) {
        return creditService.getCreditsByClient(clientId);
    }

    @Operation(summary = "Детали кредита", description = "Возвращает детальную информацию по кредиту.")
    @GetMapping("/credits/{id}")
    public CreditResponse getCredit(
            @Parameter(description = "ID кредита") @PathVariable Long id) {
        return creditService.getCreditById(id);
    }

    @Operation(summary = "Погасить кредит", description = "Досрочно погашает кредит, списывая весь остаток долга со счёта клиента.")
    @PostMapping("/credits/{id}/repay")
    public CreditResponse repayCredit(
            @Parameter(description = "ID кредита") @PathVariable Long id) {
        return creditService.repayCredit(id);
    }

    @Operation(summary = "Частичное погашение кредита", description = "Списывает указанную сумму со счёта клиента в счёт долга.")
    @PostMapping("/credits/{id}/repay/partial")
    public CreditResponse repayPartial(
            @Parameter(description = "ID кредита") @PathVariable Long id,
            @Valid @RequestBody PartialRepayRequest req) {
        return creditService.repayPartial(id, req);
    }
}