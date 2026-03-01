package com.gautama.bankhitscredit.service;


import com.gautama.bankhitscredit.config.CoreServiceClient;
import com.gautama.bankhitscredit.dto.*;
import com.gautama.bankhitscredit.entity.Credit;
import com.gautama.bankhitscredit.entity.CreditTariff;
import com.gautama.bankhitscredit.repository.CreditRepository;
import com.gautama.bankhitscredit.repository.CreditTariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;
    private final CreditTariffRepository tariffRepository;
    private final CoreServiceClient coreClient;

    @Transactional
    public TariffResponse createTariff(CreateTariffRequest req) {
        if (tariffRepository.existsByName(req.getName())) {
            throw new IllegalArgumentException("Тариф с таким названием уже существует");
        }
        CreditTariff tariff = CreditTariff.builder()
                .name(req.getName())
                .annualRate(req.getAnnualRate())
                .build();
        return TariffResponse.from(tariffRepository.save(tariff));
    }

    public List<TariffResponse> getAllTariffs() {
        return tariffRepository.findAll().stream()
                .map(TariffResponse::from)
                .toList();
    }

    @Transactional
    public CreditResponse takeCredit(TakeCreditRequest req) {
        CreditTariff tariff = tariffRepository.findById(req.getTariffId())
                .orElseThrow(() -> new IllegalArgumentException("Тариф не найден"));

        Credit credit = Credit.builder()
                .clientId(req.getClientId())
                .accountId(req.getAccountId())
                .tariff(tariff)
                .principalAmount(req.getAmount())
                .remainingDebt(req.getAmount())
                .issuedAt(LocalDateTime.now())
                .status(Credit.CreditStatus.ACTIVE)
                .build();

        return CreditResponse.from(creditRepository.save(credit));
    }

    public List<CreditResponse> getCreditsByClient(Long clientId) {
        return creditRepository.findByClientId(clientId).stream()
                .map(CreditResponse::from)
                .toList();
    }

    public CreditResponse getCreditById(Long id) {
        return creditRepository.findById(id)
                .map(CreditResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("Кредит не найден"));
    }

    @Transactional
    public CreditResponse repayCredit(Long creditId) {
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Кредит не найден"));

        if (credit.getStatus() != Credit.CreditStatus.ACTIVE) {
            throw new IllegalStateException("Кредит уже закрыт или просрочен");
        }

        boolean success = coreClient.tryWithdraw(credit.getAccountId(), credit.getRemainingDebt());
        if (!success) {
            throw new IllegalStateException("Недостаточно средств для погашения кредита");
        }

        credit.setRemainingDebt(BigDecimal.ZERO);
        credit.setStatus(Credit.CreditStatus.CLOSED);
        credit.setClosedAt(LocalDateTime.now());

        return CreditResponse.from(creditRepository.save(credit));
    }

    @Transactional
    public CreditResponse repayPartial(Long creditId, PartialRepayRequest req) {
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new IllegalArgumentException("Кредит не найден"));

        if (credit.getStatus() != Credit.CreditStatus.ACTIVE) {
            throw new IllegalStateException("Кредит уже закрыт или просрочен");
        }

        if (req.getAmount().compareTo(credit.getRemainingDebt()) > 0) {
            throw new IllegalArgumentException(
                    "Сумма " + req.getAmount() + " превышает остаток долга " + credit.getRemainingDebt()
            );
        }

        boolean success = coreClient.tryWithdraw(credit.getAccountId(), req.getAmount());
        if (!success) {
            throw new IllegalStateException("Недостаточно средств на счёте");
        }

        credit.setRemainingDebt(credit.getRemainingDebt().subtract(req.getAmount()));

        if (credit.getRemainingDebt().compareTo(BigDecimal.ZERO) == 0) {
            credit.setStatus(Credit.CreditStatus.CLOSED);
            credit.setClosedAt(LocalDateTime.now());
        }

        return CreditResponse.from(creditRepository.save(credit));
    }

    public List<CreditResponse> getAllCredits() {
        return creditRepository.findAll().stream()
                .map(CreditResponse::from)
                .toList();
    }
}