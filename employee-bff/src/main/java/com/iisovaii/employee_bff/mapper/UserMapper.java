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

    @Mapping(target = "employeeId", source = "userId")
    @Mapping(target = "firstName", source = "name")
    @Mapping(target = "lastName", ignore = true)
    EmployeeProfileResponse toEmployeeProfileResponse(UserResponse response);

    CreateClientResponse toCreateClientResponse(UserResponse response);

    @Mapping(target = "employeeId", source = "userId")
    @Mapping(target = "firstName", source = "name")
    @Mapping(target = "lastName", ignore = true)
    CreateEmployeeResponse toCreateEmployeeResponse(UserResponse response);

    @Mapping(target = "firstName", source = "name")
    @Mapping(target = "lastName", ignore = true)
    UpdateUserResponse toUpdateUserResponse(UserResponse response);

    UserStatusResponse toUserStatusResponse(UserResponse response);
}
