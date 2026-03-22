package com.iisovaii.employee_bff.mapper;

import com.iisovaii.employee_bff.dto.account.AccountDto;
import com.iisovaii.employee_bff.dto.account.AccountWithOwnerDto;
import com.iisovaii.employee_bff.dto.account.AllAccountsPageResponse;
import com.iisovaii.employee_bff.dto.operation.OperationDto;
import com.iisovaii.employee_bff.dto.response.AccountServiceResponse;
import com.iisovaii.employee_bff.dto.response.OperationServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", ignore = true)
    AccountDto toAccountDto(AccountServiceResponse response);

    List<AccountDto> toAccountDtoList(
            List<AccountServiceResponse> responses
    );

    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "ownerId", source = "clientId")
    @Mapping(target = "ownerFullName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    AccountWithOwnerDto toAccountWithOwnerDto(
            AccountServiceResponse response
    );

    @Mapping(target = "operationId", ignore = true)
    @Mapping(target = "type", source = "operationType")
    @Mapping(target = "relatedAccountId", ignore = true)
    @Mapping(target = "relatedAccountOwner", ignore = true)
    @Mapping(target = "failReason", ignore = true)
    OperationDto toOperationDto(OperationServiceResponse response);

    List<OperationDto> toOperationDtoList(
            List<OperationServiceResponse> responses
    );
}
