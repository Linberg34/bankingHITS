package com.gautama.bankhitsuser.service;

import com.gautama.bankhitsuser.dto.CreateUserRequest;
import com.gautama.bankhitsuser.dto.UpdateUserRequest;
import com.gautama.bankhitsuser.dto.UserDTO;
import com.gautama.bankhitsuser.enums.Status;
import com.gautama.bankhitsuser.enums.UserQueryType;
import com.gautama.bankhitsuser.mapper.UserMapper;
import com.gautama.bankhitsuser.model.User;
import com.gautama.bankhitsuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDTO> getUsers(UserQueryType queryType) {
        List<User> users;

        if (queryType == null || queryType == UserQueryType.ALL) {
            users = userRepository.findAll();
        } else if (queryType == UserQueryType.BANNED) {
            users = userRepository.findAllByStatus(Status.BANNED);
        } else {
            throw new IllegalArgumentException(
                    "Неизвестный тип запроса: " + queryType
            );
        }

        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Пользователь с id: " + userId + " не найден"
                ));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException(
                        "Пользователь с email: " + email + " не найден"
                ));
        return userMapper.toDto(user);
    }

    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException(
                    "Пользователь с email " + request.getEmail()
                            + " уже существует"
            );
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setStatus(Status.ACTIVE);

        return userMapper.toDto(userRepository.save(user));
    }

    public UserDTO updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Пользователь с id: " + userId + " не найден"
                ));

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        return userMapper.toDto(userRepository.save(user));
    }

    public UserDTO banUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Пользователь с id: " + userId + " не найден"
                ));

        if (user.getStatus() == Status.BANNED) {
            throw new IllegalStateException(
                    "Пользователь уже заблокирован"
            );
        }

        user.setStatus(Status.BANNED);
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDTO unbanUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Пользователь с id: " + userId + " не найден"
                ));

        if (user.getStatus() != Status.BANNED) {
            throw new IllegalStateException(
                    "Пользователь не заблокирован"
            );
        }

        user.setStatus(Status.ACTIVE);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getBannedUsers() {
        return userRepository.findAllByStatus(Status.BANNED)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
