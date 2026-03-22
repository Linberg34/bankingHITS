package com.iisovaii.employee_bff.controller;

import com.iisovaii.employee_bff.dto.employee.CreateEmployeeRequest;
import com.iisovaii.employee_bff.dto.employee.CreateEmployeeResponse;
import com.iisovaii.employee_bff.dto.employee.UpdateUserRequest;
import com.iisovaii.employee_bff.dto.employee.UpdateUserResponse;
import com.iisovaii.employee_bff.dto.employee.UserStatusResponse;
import com.iisovaii.employee_bff.security.CurrentUser;
import com.iisovaii.employee_bff.service.ProxyService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/bff/employee/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final ProxyService proxyService;

    @PostMapping
    public ResponseEntity<CreateEmployeeResponse> createEmployee(
            @Parameter(hidden = true)  @CurrentUser UUID employeeId,
            @RequestBody @Valid CreateEmployeeRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(proxyService.createEmployee(request));
    }

    @PutMapping("/{targetEmployeeId}")
    public ResponseEntity<UpdateUserResponse> updateEmployee(
            @Parameter(hidden = true)  @CurrentUser UUID employeeId,
            @PathVariable UUID targetEmployeeId,
            @RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(
                proxyService.updateUser(targetEmployeeId, request)
        );
    }

    @PostMapping("/{targetEmployeeId}/block")
    public ResponseEntity<UserStatusResponse> blockEmployee(
            @Parameter(hidden = true)  @CurrentUser UUID employeeId,
            @PathVariable UUID targetEmployeeId) {
        return ResponseEntity.ok(
                proxyService.blockUser(targetEmployeeId)
        );
    }

    @PostMapping("/{targetEmployeeId}/unblock")
    public ResponseEntity<UserStatusResponse> unblockEmployee(
            @Parameter(hidden = true)  @CurrentUser UUID employeeId,
            @PathVariable UUID targetEmployeeId) {
        return ResponseEntity.ok(
                proxyService.unblockUser(targetEmployeeId)
        );
    }
}