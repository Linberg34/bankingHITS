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
import com.iisovaii.employee_bff.infrastructure.resilience.DownstreamCallExecutor;
import com.iisovaii.employee_bff.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
    private final DownstreamCallExecutor downstreamCallExecutor;

    public EmployeeProfileResponse getEmployeeProfile(UUID employeeId) {
        UserResponse raw = callUsers("GET", "/api/users/" + employeeId, 200,
                () -> userServiceClient.getUser(employeeId));
        return userMapper.toEmployeeProfileResponse(raw);
    }

    public AllAccountsPageResponse getAllAccounts(int page, int size) {
        PageDTOAccountDTO raw = callCore(
                "GET",
                "/internal/accounts/list?page=" + page + "&size=" + size,
                200,
                () -> accountServiceClient.getAllAccounts(page, size)
        );

        List<AccountWithOwnerDto> content = raw.getContent().stream()
                .map(account -> {
                    AccountWithOwnerDto dto =
                            accountMapper.toAccountWithOwnerDto(account);
                    try {
                        UserResponse user = callUsers(
                                "GET",
                                "/api/users/" + account.getClientId(),
                                200,
                                () -> userServiceClient.getUser(account.getClientId())
                        );
                        dto.setOwnerFullName(user.getName());
                        dto.setOwnerId(user.getId());
                    } catch (Exception e) {
                        log.warn("Не удалось получить владельца счёта {}",
                                account.getAccountNumber());
                        dto.setOwnerFullName("Неизвестно");
                    }
                    return dto;
                })
                .toList();

        return new AllAccountsPageResponse(
                content,
                raw.getPageNumber(),
                raw.getPageSize(),
                raw.getTotalElements()
        );
    }

    public OperationPageResponse getOperations(
            String accountNumber, int page, int size) {
        List<OperationServiceResponse> raw = callCore(
                "GET",
                "/internal/operations/account/" + accountNumber + "?page=" + page + "&size=" + size,
                200,
                () -> accountServiceClient.getOperations(accountNumber, page, size)
        );

        List<OperationDto> content = raw.stream()
                .map(accountMapper::toOperationDto)
                .toList();

        return new OperationPageResponse(
                content,
                page,
                size,
                (long) content.size()
        );
    }

    public AccountListResponse getClientAccounts(UUID clientId) {
        List<AccountServiceResponse> raw = callCore(
                "GET",
                "/internal/accounts/by-user/" + clientId,
                200,
                () -> accountServiceClient.getAccountsByUserId(clientId)
        );
        return new AccountListResponse(accountMapper.toAccountDtoList(raw));
    }


    public ClientPageResponse getClients(int page, int size) {
        List<UserResponse> all = callUsers(
                "GET",
                "/api/users",
                200,
                () -> userServiceClient.getUsers(null)
        );

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
        UserResponse raw = callUsers(
                "GET",
                "/api/users/" + clientId,
                200,
                () -> userServiceClient.getUser(clientId)
        );
        return userMapper.toClientDetailResponse(raw);
    }

    public CreateClientResponse createClient(CreateClientRequest request) {
        callSso("POST", "/auth/register", 204, () -> {
            ssoServiceClient.register(
                    new SsoRegisterRequest(
                            request.getName(),
                            request.getEmail(),
                            request.getPassword(),
                            List.of("CLIENT")
                    )
            );
            return null;
        });

        UserResponse raw = callUsers(
                "GET",
                "/api/users/by-email?email=" + request.getEmail(),
                200,
                () -> userServiceClient.getUserByEmail(
                        request.getEmail()
                )
        );
        return userMapper.toCreateClientResponse(raw);
    }

    public CreateEmployeeResponse createEmployee(
            CreateEmployeeRequest request) {
        callSso("POST", "/auth/register", 204, () -> {
            ssoServiceClient.register(
                    new SsoRegisterRequest(
                            request.getName(),
                            request.getEmail(),
                            request.getPassword(),
                            List.of("EMPLOYEE")
                    )
            );
            return null;
        });

        UserResponse raw = callUsers(
                "GET",
                "/api/users/by-email?email=" + request.getEmail(),
                200,
                () -> userServiceClient.getUserByEmail(
                        request.getEmail()
                )
        );
        return userMapper.toCreateEmployeeResponse(raw);
    }

    public UpdateUserResponse updateUser(UUID userId, UpdateUserRequest request) {
        return userMapper.toUpdateUserResponse(
                callUsers(
                        "PUT",
                        "/api/users/" + userId,
                        200,
                        () -> userServiceClient.updateUser(userId, request)
                )
        );
    }

    public UserStatusResponse blockUser(UUID userId) {
        return userMapper.toUserStatusResponse(
                callUsers(
                        "POST",
                        "/api/users/" + userId + "/ban",
                        200,
                        () -> userServiceClient.blockUser(userId)
                )
        );
    }

    public UserStatusResponse unblockUser(UUID userId) {
        return userMapper.toUserStatusResponse(
                callUsers(
                        "POST",
                        "/api/users/" + userId + "/unban",
                        200,
                        () -> userServiceClient.unblockUser(userId)
                )
        );
    }

    public CreditListResponse getClientCredits(UUID clientId) {
        List<CreditDetailResponse> raw = callCredits(
                "GET",
                "/api/credits/client/" + clientId,
                200,
                () -> creditServiceClient.getCreditsByUserId(clientId)
        );

        List<CreditSummaryDto> credits = raw.stream()
                .map(credit -> {
                    CreditSummaryDto dto = new CreditSummaryDto();
                    dto.setCreditId(credit.getId());
                    dto.setCurrency(credit.getCurrency());
                    dto.setAmount(credit.getPrincipalAmount());
                    dto.setRemainingDebt(credit.getRemainingDebt());
                    dto.setInterestRate(credit.getAnnualRate());
                    dto.setTariffName(credit.getTariffName());
                    dto.setStatus(CreditSummaryDto.CreditStatus
                            .valueOf(credit.getStatus()));
                    dto.setNextPaymentAt(credit.getNextPaymentAt());
                    return dto;
                })
                .toList();

        return new CreditListResponse(credits);
    }

    public CreditDetailEmployeeResponse getCreditDetail(UUID creditId) {
        CreditDetailResponse credit = callCredits(
                "GET",
                "/api/credits/" + creditId,
                200,
                () -> creditServiceClient.getCreditDetailForEmployee(creditId)
        );
        CreditDetailEmployeeResponse response =
                creditMapper.toCreditDetailEmployeeResponse(credit);
        response.setOwnerFullName(callUsers(
                "GET",
                "/api/users/" + credit.getClientId(),
                200,
                () -> userServiceClient.getUser(credit.getClientId())
        ).getName());
        return response;
    }

    public List<CreditPaymentDto> getCreditPayments(UUID creditId) {
        return creditMapper.toCreditPaymentDtoList(
                callCredits(
                        "GET",
                        "/api/credits/" + creditId + "/payments",
                        200,
                        () -> creditServiceClient.getCreditPayments(creditId)
                )
        );
    }

    public CreditRatingResponse getCreditRating(UUID clientId) {
        return callCredits(
                "GET",
                "/api/credits/rating/" + clientId,
                200,
                () -> creditServiceClient.getCreditRating(clientId)
        );
    }

    public List<TariffDto> getTariffs() {
        return creditMapper.toTariffDtoList(
                callCredits(
                        "GET",
                        "/api/tariffs",
                        200,
                        creditServiceClient::getTariffs
                )
        );
    }

    public CreateTariffResponse createTariff(CreateTariffRequest request) {
        CreditServiceTariffRequest creditRequest =
                new CreditServiceTariffRequest(
                        request.getName(),
                        request.getInterestRate(),
                        request.getTermDays()
                );
        return creditMapper.toCreateTariffResponse(
                callCredits(
                        "POST",
                        "/api/tariffs",
                        201,
                        () -> creditServiceClient.createTariff(creditRequest)
                )
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

    private <T> T callSso(String method, String path, int successStatus, java.util.function.Supplier<T> action) {
        return downstreamCallExecutor.execute(
                DownstreamCallExecutor.DownstreamService.SSO,
                method,
                path,
                successStatus,
                action
        );
    }
}
