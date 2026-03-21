package com.iisovaii.client_bff.mapper.auth;

import com.iisovaii.client_bff.dto.auth.LoginUrlResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    default LoginUrlResponse toLoginUrlResponse(String loginUrl) {
        return new LoginUrlResponse(loginUrl);
    }
}

