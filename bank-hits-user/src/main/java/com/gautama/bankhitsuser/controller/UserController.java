package com.gautama.bankhitsuser.controller;

import com.gautama.bankhitsuser.dto.CreateUserRequest;
import com.gautama.bankhitsuser.dto.UpdateUserRequest;
import com.gautama.bankhitsuser.dto.UserDTO;
import com.gautama.bankhitsuser.enums.UserQueryType;
import com.gautama.bankhitsuser.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // список всех пользователей с фильтром
    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers(
            @RequestParam(required = false) UserQueryType queryType) {
        return ResponseEntity.ok(userService.getUsers(queryType));
    }

    // получить конкретного пользователя по id
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // создать клиента — вызывает BFF при регистрации клиента
    @PostMapping("/clients")
    public ResponseEntity<UserDTO> createClient(
            @RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    // создать сотрудника — вызывает BFF при создании сотрудника
    @PostMapping("/employees")
    public ResponseEntity<UserDTO> createEmployee(
            @RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    // обновить данные пользователя
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID userId,
            @RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    // заблокировать пользователя
    @PostMapping("/{userId}/ban")
    public ResponseEntity<UserDTO> banUser(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(userService.banUser(userId));
    }

    // разблокировать пользователя
    @PostMapping("/{userId}/unban")
    public ResponseEntity<UserDTO> unbanUser(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(userService.unbanUser(userId));
    }

    // список заблокированных
    @GetMapping("/banned")
    public ResponseEntity<List<UserDTO>> getBannedUsers() {
        return ResponseEntity.ok(userService.getBannedUsers());
    }
}