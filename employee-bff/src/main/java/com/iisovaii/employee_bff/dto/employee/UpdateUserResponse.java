package com.iisovaii.employee_bff.dto.employee;

import com.iisovaii.employee_bff.dto.profile.EmployeeProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private EmployeeProfileResponse.UserStatus status;
}
