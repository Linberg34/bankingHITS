package com.gautama.bankhitsuser.mapper;

import com.gautama.bankhitsuser.dto.UserDTO;
import com.gautama.bankhitsuser.dto.UserFullDTO;
import com.gautama.bankhitsuser.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO userToUserDto(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getStatus(),
                user.getRegisteredAt()
        );
    }

    public UserFullDTO userToUserFullDto(User user) {
        UserFullDTO dto = new UserFullDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        dto.setRole(user.getRole());
        dto.setRegisteredAt(user.getRegisteredAt());
        return dto;
    }
}