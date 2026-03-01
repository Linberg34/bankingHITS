package com.gautama.bankhitsuser.service;

import com.gautama.bankhitsuser.config.JwtUtil;
import com.gautama.bankhitsuser.dto.UserDTO;
import com.gautama.bankhitsuser.dto.UserFullDTO;
import com.gautama.bankhitsuser.enums.Role;
import com.gautama.bankhitsuser.enums.Status;
import com.gautama.bankhitsuser.enums.UserQueryType;
import com.gautama.bankhitsuser.mapper.UserMapper;
import com.gautama.bankhitsuser.model.User;
import com.gautama.bankhitsuser.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public User loadUserByUsername(String username) {
        return userRepository.findByEmail(username).orElseThrow(
                () -> new NoSuchElementException("Пользователь с почтой: " + username + " не найден"));
    }

    public List<UserDTO> getUsers(UserQueryType queryType) {
        List<User> users;

        if (queryType == null || queryType == UserQueryType.ALL) {
            users = userRepository.findAll();
        } else if (queryType == UserQueryType.CLIENTS) {
            users = userRepository.findAllByRole(Role.CLIENT);
        } else if (queryType == UserQueryType.EMPLOYEES) {
            users = userRepository.findAllByRole(Role.EMPLOYEE);
        } else if (queryType == UserQueryType.WITHOUT_ROLE) {
            users = userRepository.findAllByRoleIsNull();
        } else if (queryType == UserQueryType.BANNED) {
            users = userRepository.findAllByStatus(Status.BANNED);
        } else {
            throw new IllegalArgumentException("Неизвестный тип запроса пользователей: " + queryType);
        }

        return users.stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    public UserFullDTO getMe(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Неверный токен.");
        }

        String token = authHeader.substring(7);
        User user = loadUserByUsername(jwtUtil.extractUsername(token));
        return userMapper.userToUserFullDto(user);
    }

    public UserDTO banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с id: " + userId + " не найден"));

        if (user.getStatus() == Status.BANNED) {
            throw new IllegalStateException("Пользователь уже забанен");
        }

        user.setStatus(Status.BANNED);
        User bannedUser = userRepository.save(user);
        return userMapper.userToUserDto(bannedUser);
    }

    public UserDTO unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с id: " + userId + " не найден"));

        if (user.getStatus() != Status.BANNED) {
            throw new IllegalStateException("Пользователь не забанен");
        }

        user.setStatus(Status.ACTIVE);
        User unbannedUser = userRepository.save(user);
        return userMapper.userToUserDto(unbannedUser);
    }

    // Получить всех забаненных пользователей
    public List<UserDTO> getBannedUsers() {
        return userRepository.findAllByStatus(Status.BANNED).stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    // Получить статус пользователя
    public Status getUserStatus(Long userId) {
        return userRepository.findById(userId)
                .map(User::getStatus)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с id: " + userId + " не найден"));
    }
}