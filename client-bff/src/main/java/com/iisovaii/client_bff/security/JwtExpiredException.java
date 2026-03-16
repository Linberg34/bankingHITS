package com.iisovaii.client_bff.security;

public class JwtExpiredException extends RuntimeException {
    public JwtExpiredException(String message) {
        super(message);
    }
}

