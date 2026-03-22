package com.iisovaii.employee_bff.controller;

import com.iisovaii.employee_bff.dto.client.ClientDetailResponse;
import com.iisovaii.employee_bff.dto.client.ClientPageResponse;
import com.iisovaii.employee_bff.dto.client.CreateClientRequest;
import com.iisovaii.employee_bff.dto.client.CreateClientResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/bff/employee/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ProxyService proxyService;

    @GetMapping
    public ResponseEntity<ClientPageResponse> getClients(
            @Parameter(hidden = true)  @CurrentUser UUID employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                proxyService.getClients(page, size)
        );
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientDetailResponse> getClientDetail(
            @Parameter(hidden = true)  @CurrentUser UUID employeeId,
            @PathVariable UUID clientId) {
        return ResponseEntity.ok(
                proxyService.getClientDetail(clientId)
        );
    }

    @PostMapping
    public ResponseEntity<CreateClientResponse> createClient(
            @Parameter(hidden = true)  @CurrentUser UUID employeeId,
            @RequestBody @Valid CreateClientRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(proxyService.createClient(request));
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<UpdateUserResponse> updateClient(
            @Parameter(hidden = true)  @CurrentUser UUID employeeId,
            @PathVariable UUID clientId,
            @RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(
                proxyService.updateUser(clientId, request)
        );
    }

    @PostMapping("/{clientId}/block")
    public ResponseEntity<UserStatusResponse> blockClient(
            @Parameter(hidden = true)  @CurrentUser UUID employeeId,
            @PathVariable UUID clientId) {
        return ResponseEntity.ok(
                proxyService.blockUser(clientId)
        );
    }

    @PostMapping("/{clientId}/unblock")
    public ResponseEntity<UserStatusResponse> unblockClient(
            @Parameter(hidden = true)  @CurrentUser UUID employeeId,
            @PathVariable UUID clientId) {
        return ResponseEntity.ok(
                proxyService.unblockUser(clientId)
        );
    }
}