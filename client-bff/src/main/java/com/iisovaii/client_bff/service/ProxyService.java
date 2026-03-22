package com.iisovaii.client_bff.service;

import com.iisovaii.client_bff.client.AccountServiceClient;
import com.iisovaii.client_bff.client.CreditServiceClient;
import com.iisovaii.client_bff.client.UserServiceClient;
import com.iisovaii.client_bff.dto.account.AccountListResponse;
import com.iisovaii.client_bff.dto.account.CloseAccountResponse;
import com.iisovaii.client_bff.dto.account.OpenAccountRequest;
import com.iisovaii.client_bff.dto.account.OpenAccountResponse;
import com.iisovaii.client_bff.dto.account.AccountStatus;
import com.iisovaii.client_bff.dto.credit.*;
import com.iisovaii.client_bff.dto.operation.OperationDto;
import com.iisovaii.client_bff.dto.operation.OperationPageResponse;
import com.iisovaii.client_bff.dto.profile.ClientProfileResponse;
import com.iisovaii.client_bff.dto.tariff.TariffDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProxyService {

    private final AccountServiceClient accountServiceClient;
    private final CreditServiceClient creditServiceClient;
    private final UserServiceClient userServiceClient;

    public AccountListResponse getAccounts(UUID userId) {
        return new AccountListResponse(
                accountServiceClient.getAccounts(userId)
        );
    }

    public OpenAccountResponse openAccount(
            UUID userId, OpenAccountRequest request) {
        return accountServiceClient.openAccount(
                userId, request.currency().name()
        );
    }

    public CloseAccountResponse closeAccount(
            UUID userId, String accountNumber) {
        checkAccountOwnership(userId, accountNumber);
        accountServiceClient.closeAccount(accountNumber);
        return new CloseAccountResponse(accountNumber, AccountStatus.CLOSED);
    }

    public void checkAccountOwnership(UUID userId, String accountNumber) {
        var account = accountServiceClient.getAccountByNumber(accountNumber);
        if (!userId.equals(account.clientId())) {
            throw new IllegalArgumentException(
                    "Account does not belong to current user"
            );
        }
    }

    public OperationPageResponse getOperations(
            String accountNumber, int page, int size) {
        List<OperationDto> content =
                accountServiceClient.getOperations(accountNumber, page, size);
        return new OperationPageResponse(content, page, size, content.size());
    }

    public CreditListResponse getCredits(UUID userId) {
        List<CreditResponse> raw = creditServiceClient.getCredits(userId);
        List<CreditSummaryDto> credits = raw.stream()
                .map(c -> new CreditSummaryDto(
                        c.id(),
                        c.principalAmount(),   // amount <- principalAmount
                        c.remainingDebt(),
                        c.annualRate(),        // interestRate <- annualRate
                        c.tariffName(),
                        c.status(),
                        c.nextPaymentAt()
                ))
                .toList();
        return new CreditListResponse(credits);
    }

    public CreditDetailResponse getCreditDetail(UUID userId, UUID creditId) {
        CreditResponse raw = creditServiceClient.getCreditDetail(creditId);

        if (!userId.equals(raw.clientId())) {
            throw new IllegalArgumentException(
                    "Credit does not belong to current user"
            );
        }

        List<CreditPaymentDto> payments = creditServiceClient
                .getCreditPayments(creditId)
                .stream()
                .map(p -> new CreditPaymentDto(
                        p.id(),
                        p.amount(),
                        p.dueAt(),
                        p.paidAt(),
                        p.status()
                ))
                .toList();

        return new CreditDetailResponse(
                raw.id(),
                raw.clientId(),
                raw.accountNumber(),
                raw.principalAmount(),
                raw.remainingDebt(),
                raw.annualRate(),
                raw.tariffName(),
                raw.status(),
                raw.issuedAt(),
                raw.nextPaymentAt(),
                payments
        );
    }

    public void checkCreditOwnership(UUID userId, UUID creditId) {
        CreditResponse raw = creditServiceClient.getCreditDetail(creditId);
        if (!userId.equals(raw.clientId())) {
            throw new IllegalArgumentException(
                    "Credit does not belong to current user"
            );
        }
    }

    public List<CreditPaymentDto> getCreditPayments(UUID creditId) {
        return creditServiceClient.getCreditPayments(creditId)
                .stream()
                .map(p -> new CreditPaymentDto(
                        p.id(),
                        p.amount(),
                        p.dueAt(),
                        p.paidAt(),
                        p.status()
                ))
                .toList();
    }

    public TakeCreditResponse takeCredit(
            UUID userId, TakeCreditRequest request) {
        CreditResponse raw = creditServiceClient.takeCredit(
                new TakeCreditPayload(
                        userId,
                        request.accountNumber(),
                        request.tariffId(),
                        request.amount()
                )
        );
        return new TakeCreditResponse(
                raw.id(),
                raw.principalAmount(),
                raw.remainingDebt(),
                raw.annualRate(),
                raw.tariffName(),
                raw.status(),
                raw.nextPaymentAt()
        );
    }

    public RepayCreditResponse repayCredit(
            UUID userId, UUID creditId, RepayCreditRequest request) {
        checkCreditOwnership(userId, creditId);

        CreditResponse raw;
        if (request != null && request.amount() != null) {
            raw = creditServiceClient.repayCreditPartial(
                    creditId,
                    new PartialRepayPayload(request.amount())
            );
        } else {
            raw = creditServiceClient.repayCredit(creditId);
        }

        return new RepayCreditResponse(
                raw.id(),
                raw.remainingDebt(),
                raw.status()
        );
    }

    public CreditRatingResponse getCreditRating(UUID userId) {
        return creditServiceClient.getCreditRating(userId);
    }

    public List<TariffDto> getTariffs() {
        return creditServiceClient.getTariffs()
                .stream()
                .map(t -> new TariffDto(
                        t.id(),
                        t.name(),
                        t.annualRate(),   // annualRate -> interestRate
                        t.termDays()
                ))
                .toList();
    }

    public ClientProfileResponse getClientProfile(UUID userId) {
        return userServiceClient.getUser(userId);
    }
}
