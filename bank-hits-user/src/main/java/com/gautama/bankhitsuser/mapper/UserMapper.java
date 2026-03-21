package com.gautama.bankhitsuser.mapper;

import com.gautama.bankhitsuser.dto.UserDTO;
import com.gautama.bankhitsuser.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    UserDTO toDto(User user);
}