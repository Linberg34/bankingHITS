package com.iisovaii.employee_bff.controller;

import com.iisovaii.employee_bff.dto.profile.EmployeeProfileResponse;
import com.iisovaii.employee_bff.security.CurrentUser;
import com.iisovaii.employee_bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/bff/employee")
@RequiredArgsConstructor
public class EmployeeProfileController {

    private final ProxyService proxyService;

    @GetMapping("/profile")
    public ResponseEntity<EmployeeProfileResponse> getProfile(
            @CurrentUser UUID employeeId) {
        return ResponseEntity.ok(
                proxyService.getEmployeeProfile(employeeId)
        );
    }
}