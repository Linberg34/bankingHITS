package com.iisovaii.employee_bff.service;

import com.iisovaii.employee_bff.client.AccountServiceClient;
import com.iisovaii.employee_bff.client.CreditServiceClient;
import com.iisovaii.employee_bff.client.SsoServiceClient;
import com.iisovaii.employee_bff.client.UserServiceClient;
import com.iisovaii.employee_bff.dto.SsoRegisterRequest;
import com.iisovaii.employee_bff.dto.account.*;
import com.iisovaii.employee_bff.dto.client.*;
import com.iisovaii.employee_bff.dto.credit.*;
import com.iisovaii.employee_bff.dto.employee.*;
import com.iisovaii.employee_bff.dto.operation.*;
import com.iisovaii.employee_bff.dto.profile.*;
import com.iisovaii.employee_bff.dto.response.*;
import com.iisovaii.employee_bff.dto.tariff.*;
import com.iisovaii.employee_bff.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;  // явно указываем этот импорт

@Service
@RequiredArgsConstructor
@Slf4j
public class ProxyService {
    private final AccountServiceClient accountServiceClient;
    private final CreditServiceClient creditServiceClient;
    private final UserServiceClient userServiceClient;
    private final AccountMapper accountMapper;
    private final CreditMapper creditMapper;
    private final UserMapper userMapper;
    private final SsoServiceClient ssoServiceClient;

    public EmployeeProfileResponse getEmployeeProfile(UUID employeeId) {
        UserResponse raw = userServiceClient.getUser(employeeId);
        return userMapper.toEmployeeProfileResponse(raw);
    }

    public AllAccountsPageResponse getAllAccounts(int page, int size) {
        PageResponse<AccountWithOwnerResponse> raw =
                accountServiceClient.getAllAccounts(page, size);
        return accountMapper.toAllAccountsPageResponse(raw);
    }

    public AccountListResponse getClientAccounts(UUID clientId) {
        List<AccountResponse> raw =
                accountServiceClient.getAccountsByUserId(clientId);
        return new AccountListResponse(accountMapper.toAccountDtoList(raw));
    }

    public OperationPageResponse getOperations(
            UUID accountId, int page, int size) {
        PageResponse<OperationResponse> raw =
                accountServiceClient.getOperations(accountId, page, size);
        return accountMapper.toOperationPageResponse(raw);
    }

    public ClientPageResponse getClients(int page, int size) {
        // UserService возвращает List, не Page
        // делаем ручную пагинацию на стороне BFF
        List<UserResponse> all = userServiceClient.getUsers(null);

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, all.size());

        List<ClientSummaryDto> content = all.stream()
                .skip(fromIndex)
                .limit(size)
                .map(userMapper::toClientSummaryDto)
                .toList();

        return new ClientPageResponse(
                content,
                page,
                size,
                all.size()
        );
    }

    public ClientDetailResponse getClientDetail(UUID clientId) {
        UserResponse raw = userServiceClient.getUser(clientId);
        return userMapper.toClientDetailResponse(raw);
    }

    public CreateClientResponse createClient(CreateClientRequest request) {
        // шаг 1 — создаём credentials в SSO
        ssoServiceClient.register(
                new SsoRegisterRequest(
                        request.getEmail(),
                        request.getPassword(),
                        List.of("CLIENT")
                )
        );

        // шаг 2 — создаём профиль в UserService без пароля
        UserResponse raw = userServiceClient.createClient(
                new CreateUserInServiceRequest(
                        request.getName(),
                        request.getEmail()
                )
        );
        return userMapper.toCreateClientResponse(raw);
    }

    public CreateEmployeeResponse createEmployee(
            CreateEmployeeRequest request) {
        // аналогично клиенту — сначала SSO, потом UserService
        ssoServiceClient.register(
                new SsoRegisterRequest(
                        request.getEmail(),
                        request.getPassword(),
                        List.of("EMPLOYEE")
                )
        );

        UserResponse raw = userServiceClient.createEmployee(
                new CreateUserInServiceRequest(
                        request.getName(),
                        request.getEmail()
                )
        );
        return userMapper.toCreateEmployeeResponse(raw);
    }

    public UpdateUserResponse updateUser(UUID userId, UpdateUserRequest request) {
        return userMapper.toUpdateUserResponse(
                userServiceClient.updateUser(userId, request)
        );
    }

    public UserStatusResponse blockUser(UUID userId) {
        return userMapper.toUserStatusResponse(
                userServiceClient.blockUser(userId)
        );
    }

    public UserStatusResponse unblockUser(UUID userId) {
        return userMapper.toUserStatusResponse(
                userServiceClient.unblockUser(userId)
        );
    }

    public CreditListResponse getClientCredits(UUID clientId) {
        List<CreditSummaryResponse> raw =
                creditServiceClient.getCreditsByUserId(clientId);
        return new CreditListResponse(creditMapper.toCreditSummaryDtoList(raw));
    }

    public CreditDetailEmployeeResponse getCreditDetail(UUID creditId) {
        CreditDetailResponse credit = creditServiceClient.getCreditDetailForEmployee(creditId);
        CreditDetailEmployeeResponse response =
                creditMapper.toCreditDetailEmployeeResponse(credit);
        response.setOwnerFullName(userServiceClient.getUser(credit.getClientId()).getName());
        return response;
    }

    public List<CreditPaymentDto> getCreditPayments(UUID creditId) {
        return creditMapper.toCreditPaymentDtoList(
                creditServiceClient.getCreditPayments(creditId)
        );
    }

    public CreditRatingResponse getCreditRating(UUID clientId) {
        return creditServiceClient.getCreditRating(clientId);
    }

    public List<TariffDto> getTariffs() {
        return creditMapper.toTariffDtoList(
                creditServiceClient.getTariffs()
        );
    }

    public CreateTariffResponse createTariff(CreateTariffRequest request) {
        return creditMapper.toCreateTariffResponse(
                creditServiceClient.createTariff(request)
        );
    }
}
