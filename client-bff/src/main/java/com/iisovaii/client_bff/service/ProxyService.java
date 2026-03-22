package com.iisovaii.client_bff.service;

import com.iisovaii.client_bff.client.AccountServiceClient;
import com.iisovaii.client_bff.dto.credit.CreditTakeCreditPayload;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProxyService {

    private final AccountServiceClient accountServiceClient;
    private final CreditServiceClient creditServiceClient;
    private final UserServiceClient userServiceClient;

    public ProxyService(
            AccountServiceClient accountServiceClient,
            CreditServiceClient creditServiceClient,
            UserServiceClient userServiceClient
    ) {
        this.accountServiceClient = accountServiceClient;
        this.creditServiceClient = creditServiceClient;
        this.userServiceClient = userServiceClient;
    }

    public AccountListResponse getAccounts(UUID userId) {
        return new AccountListResponse(accountServiceClient.getAccounts(userId));
    }

    public OpenAccountResponse openAccount(UUID userId, OpenAccountRequest request) {
        return accountServiceClient.openAccount(userId, request.currency().name());
    }

    public CloseAccountResponse closeAccount(UUID userId, String accountNumber) {
        checkAccountOwnership(userId, accountNumber);
        accountServiceClient.closeAccount(accountNumber);
        return new CloseAccountResponse(accountNumber, AccountStatus.CLOSED);
    }

    public CreditListResponse getCredits(UUID userId) {
        return new CreditListResponse(creditServiceClient.getCredits(userId));
    }

    public CreditDetailResponse getCreditDetail(UUID userId, UUID creditId) {
        CreditDetailResponse credit = creditServiceClient.getCreditDetail(creditId);
        if (!userId.equals(credit.clientId())) {
            throw new IllegalArgumentException("Credit does not belong to current user");
        }

        List<CreditPaymentDto> payments = creditServiceClient.getCreditPayments(creditId);
        return new CreditDetailResponse(
                credit.creditId(),
                credit.clientId(),
                credit.accountNumber(),
                credit.amount(),
                credit.remainingDebt(),
                credit.interestRate(),
                credit.tariffName(),
                credit.status(),
                credit.issuedAt(),
                credit.nextPaymentAt(),
                payments
        );
    }

    public void checkCreditOwnership(UUID userId, UUID creditId) {
        CreditDetailResponse credit = creditServiceClient.getCreditDetail(creditId);
        if (!userId.equals(credit.clientId())) {
            throw new IllegalArgumentException("Credit does not belong to current user");
        }
    }

    public List<CreditPaymentDto> getCreditPayments(UUID creditId) {
        return creditServiceClient.getCreditPayments(creditId);
    }

    public TakeCreditResponse takeCredit(UUID userId, TakeCreditRequest request) {
        return creditServiceClient.takeCredit(new CreditTakeCreditPayload(
                userId,
                request.accountNumber(),
                request.tariffId(),
                request.amount()
        ));
    }

    public void checkAccountOwnership(UUID userId, String accountNumber) {
        if (!userId.equals(accountServiceClient.getAccountByNumber(accountNumber).userId())) {
            throw new IllegalArgumentException("Account does not belong to current user");
        }
    }

    public RepayCreditResponse repayCredit(UUID userId, UUID creditId, RepayCreditRequest request) {
        checkCreditOwnership(userId, creditId);
        if (request != null && request.amount() != null) {
            return creditServiceClient.repayCreditPartial(creditId, request);
        }
        return creditServiceClient.repayCredit(creditId);
    }

    public CreditRatingResponse getCreditRating(UUID userId) {
        return creditServiceClient.getCreditRating(userId);
    }

    public List<TariffDto> getTariffs() {
        return creditServiceClient.getTariffs();
    }

    public ClientProfileResponse getClientProfile(UUID userId) {
        return userServiceClient.getUser(userId);
    }

    public OperationPageResponse getOperations(String accountNumber, int page, int size) {
        List<OperationDto> content = accountServiceClient.getOperations(accountNumber, page, size);
        return new OperationPageResponse(content, page, size, content.size());
    }
}
