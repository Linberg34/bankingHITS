package com.iisovaii.employee_bff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SsoRegisterRequest {
    private String username;   // email
    private String password;
    private List<String> roles;
}