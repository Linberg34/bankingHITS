package com.gautama.bankhitsuser.controller;

import com.gautama.bankhitsuser.dto.UserDTO;
import com.gautama.bankhitsuser.dto.UserFullDTO;
import com.gautama.bankhitsuser.enums.Role;
import com.gautama.bankhitsuser.enums.UserQueryType;
import com.gautama.bankhitsuser.model.User;
import com.gautama.bankhitsuser.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers(
            @RequestParam(required = false) UserQueryType queryType
    ) {
        List<UserDTO> users = userService.getUsers(queryType);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<UserFullDTO> me(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getMe(request));
    }

    @PostMapping("/{userId}/ban")
    public ResponseEntity<UserDTO> banUser(@PathVariable Long userId) {
        UserDTO bannedUser = userService.banUser(userId);
        return ResponseEntity.ok(bannedUser);
    }

    // Разбан пользователя
    @PostMapping("/{userId}/unban")
    public ResponseEntity<UserDTO> unbanUser(@PathVariable Long userId) {
        UserDTO unbannedUser = userService.unbanUser(userId);
        return ResponseEntity.ok(unbannedUser);
    }

    // Получить всех забаненных пользователей
    @GetMapping("/banned")
    public ResponseEntity<List<UserDTO>> getBannedUsers() {
        List<UserDTO> bannedUsers = userService.getBannedUsers();
        return ResponseEntity.ok(bannedUsers);
    }
    //
    //    @PostMapping("/batch")
    //    public List<UserDTO> getByIds(@RequestBody List<Long> ids) {
    //        return userService.getDtosByIds(ids);
    //    }
}