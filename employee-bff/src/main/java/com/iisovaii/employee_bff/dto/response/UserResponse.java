package com.iisovaii.employee_bff.dto.response;

import com.iisovaii.employee_bff.dto.profile.EmployeeProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID userId;
    private String name;
    private String email;
    private EmployeeProfileResponse.UserStatus status;
    private LocalDateTime registeredAt;
    private int accountCount;        // приходит из UserService
    private int activeCreditCount;   // приходит из UserService
}
