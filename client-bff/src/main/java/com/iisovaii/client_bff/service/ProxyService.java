package com.iisovaii.client_bff.service;

import com.iisovaii.client_bff.client.AccountServiceClient;
import com.iisovaii.client_bff.client.CreditServiceClient;
import com.iisovaii.client_bff.client.UserServiceClient;
import com.iisovaii.client_bff.dto.account.AccountListResponse;
import com.iisovaii.client_bff.dto.account.CloseAccountResponse;
import com.iisovaii.client_bff.dto.account.OpenAccountRequest;
import com.iisovaii.client_bff.dto.account.OpenAccountResponse;
import com.iisovaii.client_bff.dto.credit.*;
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
        return accountServiceClient.getAccounts(userId);
    }

    public OpenAccountResponse openAccount(UUID userId, OpenAccountRequest request) {
        return accountServiceClient.openAccount(userId, request);
    }

    public CloseAccountResponse closeAccount(UUID userId, UUID accountId) {
        return accountServiceClient.closeAccount(accountId, userId);
    }

    public CreditListResponse getCredits(UUID userId) {
        return creditServiceClient.getCredits(userId);
    }

    public CreditDetailResponse getCreditDetail(UUID userId, UUID creditId) {
        return creditServiceClient.getCreditDetail(creditId, userId);
    }

    public void checkCreditOwnership(UUID userId, UUID creditId) {
        creditServiceClient.checkCreditOwnership(creditId, userId);
    }

    public List<CreditPaymentDto> getCreditPayments(UUID creditId) {
        return creditServiceClient.getCreditPayments(creditId);
    }

    public TakeCreditResponse takeCredit(UUID userId, TakeCreditRequest request) {
        return creditServiceClient.takeCredit(userId, request);
    }

    public void checkAccountOwnership(UUID userId, UUID accountId) {
        accountServiceClient.checkAccountOwnership(accountId, userId);
    }

    public RepayCreditResponse repayCredit(UUID userId, UUID creditId, RepayCreditRequest request) {
        return creditServiceClient.repayCredit(creditId, userId, request);
    }

    public CreditRatingResponse getCreditRating(UUID userId) {
        return creditServiceClient.getCreditRating(userId);
    }

    public List<TariffDto> getTariffs() {
        return userServiceClient.getTariffs();
    }

    public ClientProfileResponse getClientProfile(UUID userId) {
        return userServiceClient.getClientProfile(userId);
    }

    public OperationPageResponse getOperations(UUID accountId, int page, int size) {
        return accountServiceClient.getOperations(accountId, page, size);
    }
}