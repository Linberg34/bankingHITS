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

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // для ClientSummaryDto accountCount и activeCreditCount
    // приходят отдельно — их маппим явно
    @Mapping(target = "accountCount", source = "accountCount")
    @Mapping(target = "activeCreditCount", source = "activeCreditCount")
    ClientSummaryDto toClientSummaryDto(UserResponse response);

    List<ClientSummaryDto> toClientSummaryDtoList(List<UserResponse> responses);

    ClientDetailResponse toClientDetailResponse(UserResponse response);

    // employeeId маппим из userId — имена разные
    @Mapping(target = "employeeId", source = "userId")
    EmployeeProfileResponse toEmployeeProfileResponse(UserResponse response);

    CreateClientResponse toCreateClientResponse(UserResponse response);

    // employeeId маппим из userId
    @Mapping(target = "employeeId", source = "userId")
    CreateEmployeeResponse toCreateEmployeeResponse(UserResponse response);

    UpdateUserResponse toUpdateUserResponse(UserResponse response);

    UserStatusResponse toUserStatusResponse(UserResponse response);
}
