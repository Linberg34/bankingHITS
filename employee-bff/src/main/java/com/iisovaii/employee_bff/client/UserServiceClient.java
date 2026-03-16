package com.iisovaii.employee_bff.client;

import com.iisovaii.employee_bff.config.FeignConfig;
import com.iisovaii.employee_bff.dto.client.CreateClientRequest;
import com.iisovaii.employee_bff.dto.employee.CreateEmployeeRequest;
import com.iisovaii.employee_bff.dto.employee.UpdateUserRequest;
import com.iisovaii.employee_bff.dto.response.PageResponse;
import com.iisovaii.employee_bff.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
        name = "user-service",
        url = "${services.user-service-url}",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserResponse getUser(@PathVariable UUID userId);

    @GetMapping("/users")
    PageResponse<UserResponse> getClients(
            @RequestParam int page,
            @RequestParam int size
    );

    @PostMapping("/users")
    UserResponse createClient(@RequestBody CreateClientRequest request);

    @PostMapping("/users/employees")
    UserResponse createEmployee(@RequestBody CreateEmployeeRequest request);

    @PutMapping("/users/{userId}")
    UserResponse updateUser(
            @PathVariable UUID userId,
            @RequestBody UpdateUserRequest request
    );

    @PostMapping("/users/{userId}/block")
    UserResponse blockUser(@PathVariable UUID userId);

    @PostMapping("/users/{userId}/unblock")
    UserResponse unblockUser(@PathVariable UUID userId);
}
