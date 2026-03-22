package com.iisovaii.employee_bff.client;

import com.iisovaii.employee_bff.config.FeignConfig;
import com.iisovaii.employee_bff.dto.client.CreateUserInServiceRequest;
import com.iisovaii.employee_bff.dto.employee.UpdateUserRequest;
import com.iisovaii.employee_bff.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// client/UserServiceClient.java
@FeignClient(
        name = "user-service",
        url = "${services.user-service-url}",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}")
    UserResponse getUser(
            @PathVariable("userId") UUID userId
    );

    @GetMapping("/api/users/by-email")
    UserResponse getUserByEmail(
            @RequestParam("email") String email
    );

    @GetMapping("/api/users")
    List<UserResponse> getUsers(
            @RequestParam(value = "queryType", required = false)
            String queryType
    );

    @PostMapping("/api/users/clients")
    UserResponse createClient(
            @RequestBody CreateUserInServiceRequest request
    );

    @PostMapping("/api/users/employees")
    UserResponse createEmployee(
            @RequestBody CreateUserInServiceRequest request
    );

    @PutMapping("/api/users/{userId}")
    UserResponse updateUser(
            @PathVariable("userId") UUID userId,
            @RequestBody UpdateUserRequest request
    );

    @PostMapping("/api/users/{userId}/ban")
    UserResponse blockUser(
            @PathVariable("userId") UUID userId
    );

    @PostMapping("/api/users/{userId}/unban")
    UserResponse unblockUser(
            @PathVariable("userId") UUID userId
    );
}
