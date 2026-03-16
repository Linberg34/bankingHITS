package com.iisovaii.employee_bff.dto.client;

import com.iisovaii.employee_bff.dto.profile.EmployeeProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientSummaryDto {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private EmployeeProfileResponse.UserStatus status;
    private int accountCount;
    private int activeCreditCount;
}
