package com.iisovaii.client_bff.config;

import java.security.Principal;

// простая реализация Principal для STOMP
public record StompPrincipal(String name) implements Principal {

    @Override
    public String getName() {
        return name;
    }
}
