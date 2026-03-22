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

// mapper/UserMapper.java
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "accountCount", ignore = true)
    @Mapping(target = "activeCreditCount", ignore = true)
    ClientSummaryDto toClientSummaryDto(UserResponse response);

    @Mapping(target = "userId", source = "id")
    ClientDetailResponse toClientDetailResponse(UserResponse response);

    @Mapping(target = "employeeId", source = "id")
    EmployeeProfileResponse toEmployeeProfileResponse(UserResponse response);

    @Mapping(target = "userId", source = "id")
    CreateClientResponse toCreateClientResponse(UserResponse response);

    @Mapping(target = "employeeId", source = "id")
    CreateEmployeeResponse toCreateEmployeeResponse(UserResponse response);

    @Mapping(target = "userId", source = "id")
    UpdateUserResponse toUpdateUserResponse(UserResponse response);

    @Mapping(target = "userId", source = "id")
    UserStatusResponse toUserStatusResponse(UserResponse response);
}
