package com.iisovaii.employee_bff.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserInServiceRequest {
    private String name;
    private String email;
}