package com.iisovaii.employee_bff.mapper;

import com.iisovaii.employee_bff.dto.client.ClientDetailResponse;
import com.iisovaii.employee_bff.dto.client.ClientSummaryDto;
import com.iisovaii.employee_bff.dto.client.CreateClientResponse;
import com.iisovaii.employee_bff.dto.employee.CreateEmployeeResponse;
import com.iisovaii.employee_bff.dto.employee.UpdateUserResponse;
import com.iisovaii.employee_bff.dto.employee.UserStatusResponse;
import com.iisovaii.employee_bff.dto.profile.EmployeeProfileResponse;
import com.iisovaii.employee_bff.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "accountCount", ignore = true)
    @Mapping(target = "activeCreditCount", ignore = true)
    ClientSummaryDto toClientSummaryDto(UserResponse response);

    // userId -> userId автоматически
    ClientDetailResponse toClientDetailResponse(UserResponse response);

    // userId -> employeeId — разные имена, нужен явный маппинг
    @Mapping(target = "employeeId", source = "userId")
    EmployeeProfileResponse toEmployeeProfileResponse(UserResponse response);

    // userId -> userId автоматически
    CreateClientResponse toCreateClientResponse(UserResponse response);

    // userId -> employeeId
    @Mapping(target = "employeeId", source = "userId")
    CreateEmployeeResponse toCreateEmployeeResponse(UserResponse response);

    // userId -> userId автоматически
    UpdateUserResponse toUpdateUserResponse(UserResponse response);

    // userId -> userId автоматически
    UserStatusResponse toUserStatusResponse(UserResponse response);
}