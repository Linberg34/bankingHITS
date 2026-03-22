package com.iisovaii.employee_bff.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfileResponse {
    private UUID employeeId;
    private String name;
    private String email;
    private UserStatus status;

    public enum UserStatus {
        ACTIVE, BLOCKED
    }
}
