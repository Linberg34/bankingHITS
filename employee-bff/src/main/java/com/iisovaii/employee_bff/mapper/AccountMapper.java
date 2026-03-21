package com.iisovaii.employee_bff.mapper;

import com.iisovaii.employee_bff.dto.account.AccountDto;
import com.iisovaii.employee_bff.dto.account.AccountWithOwnerDto;
import com.iisovaii.employee_bff.dto.account.AllAccountsPageResponse;
import com.iisovaii.employee_bff.dto.account.CloseAccountResponse;
import com.iisovaii.employee_bff.dto.operation.OperationDto;
import com.iisovaii.employee_bff.dto.operation.OperationPageResponse;
import com.iisovaii.employee_bff.dto.response.AccountResponse;
import com.iisovaii.employee_bff.dto.response.AccountWithOwnerResponse;
import com.iisovaii.employee_bff.dto.response.OperationResponse;
import com.iisovaii.employee_bff.dto.response.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

// mapper/AccountMapper.java
@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDto toAccountDto(AccountResponse response);

    List<AccountDto> toAccountDtoList(List<AccountResponse> responses);

    // ownerFullName собираем из двух полей — нужен кастомный маппинг
    @Mapping(
            target = "ownerFullName",
            expression = "java(response.getFirstName() + \" \" + response.getLastName())"
    )
    AccountWithOwnerDto toAccountWithOwnerDto(AccountWithOwnerResponse response);

    @Mapping(target = "content", source = "content")
    @Mapping(target = "page", source = "page")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "totalElements", source = "totalElements")
    AllAccountsPageResponse toAllAccountsPageResponse(
            PageResponse<AccountWithOwnerResponse> response
    );

    OperationDto toOperationDto(OperationResponse response);

    @Mapping(target = "content", source = "content")
    OperationPageResponse toOperationPageResponse(
            PageResponse<OperationResponse> response
    );

    CloseAccountResponse toCloseAccountResponse(AccountResponse response);
}
