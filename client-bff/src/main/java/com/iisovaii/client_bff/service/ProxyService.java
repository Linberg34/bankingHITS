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
import com.iisovaii.client_bff.dto.operation.OperationPageResponse;
import com.iisovaii.client_bff.dto.profile.ClientProfileResponse;
import com.iisovaii.client_bff.dto.tariff.TariffDto;
import com.iisovaii.client_bff.infrastructure.resilience.DownstreamCallExecutor;
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
    private final DownstreamCallExecutor downstreamCallExecutor;

    public AccountListResponse getAccounts(UUID userId) {
        return new AccountListResponse(
                callCore("GET", "/internal/accounts/by-user/" + userId, 200,
                        () -> accountServiceClient.getAccounts(userId))
        );
    }

    public OpenAccountResponse openAccount(
            UUID userId, OpenAccountRequest request) {
        return callCore("POST", "/internal/accounts/current", 200,
                () -> accountServiceClient.openAccount(
                        userId, request.currency().name()
                )
        );
    }

    public CloseAccountResponse closeAccount(
            UUID userId, String accountNumber) {
        checkAccountOwnership(userId, accountNumber);
        callCore("DELETE", "/internal/accounts/" + accountNumber, 204, () -> {
            accountServiceClient.closeAccount(accountNumber);
            return null;
        });
        return new CloseAccountResponse(accountNumber, AccountStatus.CLOSED);
    }

    public void checkAccountOwnership(UUID userId, String accountNumber) {
        var account = callCore(
                "GET",
                "/internal/accounts/number/" + accountNumber,
                200,
                () -> accountServiceClient.getAccountByNumber(accountNumber)
        );
        if (!userId.equals(account.clientId())) {
            throw new IllegalArgumentException(
                    "Account does not belong to current user"
            );
        }
    }

    public OperationPageResponse getOperations(
            String accountNumber, int page, int size) {
        var pageDto = callCore(
                "GET",
                "/internal/operations/account/" + accountNumber + "/page?page=" + page + "&size=" + size,
                200,
                () -> accountServiceClient.getOperations(accountNumber, page, size)
        );
        return new OperationPageResponse(
                pageDto.content(),
                pageDto.pageNumber(),
                pageDto.pageSize(),
                pageDto.totalElements()
        );
    }

    public CreditListResponse getCredits(UUID userId) {
        List<CreditResponse> raw = callCredits(
                "GET",
                "/api/credits/client/" + userId,
                200,
                () -> creditServiceClient.getCredits(userId)
        );
        List<CreditSummaryDto> credits = raw.stream()
                .map(c -> new CreditSummaryDto(
                        c.id(),
                        c.accountNumber(),
                        c.currency(),
                        c.principalAmount(),
                        c.remainingDebt(),
                        c.annualRate(),
                        c.tariffName(),
                        c.status(),
                        c.nextPaymentAt()
                ))
                .toList();
        return new CreditListResponse(credits);
    }

    public CreditDetailResponse getCreditDetail(UUID userId, UUID creditId) {
        CreditResponse raw = callCredits(
                "GET",
                "/api/credits/" + creditId,
                200,
                () -> creditServiceClient.getCreditDetail(creditId)
        );

        if (!userId.equals(raw.clientId())) {
            throw new IllegalArgumentException(
                    "Credit does not belong to current user"
            );
        }

        List<CreditPaymentDto> payments = callCredits(
                "GET",
                "/api/credits/" + creditId + "/payments",
                200,
                () -> creditServiceClient.getCreditPayments(creditId)
        )
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
        CreditResponse raw = callCredits(
                "GET",
                "/api/credits/" + creditId,
                200,
                () -> creditServiceClient.getCreditDetail(creditId)
        );
        if (!userId.equals(raw.clientId())) {
            throw new IllegalArgumentException(
                    "Credit does not belong to current user"
            );
        }
    }

    public List<CreditPaymentDto> getCreditPayments(UUID creditId) {
        return callCredits(
                "GET",
                "/api/credits/" + creditId + "/payments",
                200,
                () -> creditServiceClient.getCreditPayments(creditId)
        )
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
        CreditResponse raw = callCredits(
                "POST",
                "/api/credits",
                201,
                () -> creditServiceClient.takeCredit(
                        new TakeCreditPayload(
                                userId,
                                request.accountNumber(),
                                request.tariffId(),
                                request.amount()
                        )
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
            raw = callCredits(
                    "POST",
                    "/api/credits/" + creditId + "/repay/partial",
                    200,
                    () -> creditServiceClient.repayCreditPartial(
                            creditId,
                            new PartialRepayPayload(request.amount())
                    )
            );
        } else {
            raw = callCredits(
                    "POST",
                    "/api/credits/" + creditId + "/repay",
                    200,
                    () -> creditServiceClient.repayCredit(creditId)
            );
        }

        return new RepayCreditResponse(
                raw.id(),
                raw.remainingDebt(),
                raw.status()
        );
    }

    public CreditRatingResponse getCreditRating(UUID userId) {
        return callCredits(
                "GET",
                "/api/credits/rating/" + userId,
                200,
                () -> creditServiceClient.getCreditRating(userId)
        );
    }

    public List<TariffDto> getTariffs() {
        return callCredits(
                "GET",
                "/api/tariffs",
                200,
                creditServiceClient::getTariffs
        )
                .stream()
                .map(t -> new TariffDto(
                        t.id(),
                        t.name(),
                        t.annualRate(),
                        t.termDays()
                ))
                .toList();
    }

    public ClientProfileResponse getClientProfile(UUID userId) {
        return callUsers(
                "GET",
                "/api/users/" + userId,
                200,
                () -> userServiceClient.getUser(userId)
        );
    }

    private <T> T callCore(String method, String path, int successStatus, java.util.function.Supplier<T> action) {
        return downstreamCallExecutor.execute(
                DownstreamCallExecutor.DownstreamService.CORE,
                method,
                path,
                successStatus,
                action
        );
    }

    private <T> T callCredits(String method, String path, int successStatus, java.util.function.Supplier<T> action) {
        return downstreamCallExecutor.execute(
                DownstreamCallExecutor.DownstreamService.CREDITS,
                method,
                path,
                successStatus,
                action
        );
    }

    private <T> T callUsers(String method, String path, int successStatus, java.util.function.Supplier<T> action) {
        return downstreamCallExecutor.execute(
                DownstreamCallExecutor.DownstreamService.USERS,
                method,
                path,
                successStatus,
                action
        );
    }
}
