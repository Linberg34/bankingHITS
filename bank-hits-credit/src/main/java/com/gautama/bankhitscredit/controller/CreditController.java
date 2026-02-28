package com.gautama.bankhitscredit.controller;


import com.gautama.bankhitscredit.dto.CreateTariffRequest;
import com.gautama.bankhitscredit.dto.CreditResponse;
import com.gautama.bankhitscredit.dto.TakeCreditRequest;
import com.gautama.bankhitscredit.dto.TariffResponse;
import com.gautama.bankhitscredit.service.CreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @PostMapping("/tariffs")
    @ResponseStatus(HttpStatus.CREATED)
    public TariffResponse createTariff(@Valid @RequestBody CreateTariffRequest req) {
        return creditService.createTariff(req);
    }

    @GetMapping("/tariffs")
    public List<TariffResponse> getAllTariffs() {
        return creditService.getAllTariffs();
    }

    @GetMapping("/credits")
    public List<CreditResponse> getAllCredits() {
        return creditService.getAllCredits();
    }

    @PostMapping("/credits")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditResponse takeCredit(@Valid @RequestBody TakeCreditRequest req) {
        return creditService.takeCredit(req);
    }

    @GetMapping("/credits/client/{clientId}")
    public List<CreditResponse> getClientCredits(@PathVariable Long clientId) {
        return creditService.getCreditsByClient(clientId);
    }

    @GetMapping("/credits/{id}")
    public CreditResponse getCredit(@PathVariable Long id) {
        return creditService.getCreditById(id);
    }

    @PostMapping("/credits/{id}/repay")
    public CreditResponse repayCredit(@PathVariable Long id) {
        return creditService.repayCredit(id);
    }
}