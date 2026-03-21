package com.iisovaii.employee_bff.ws;


import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WsSessionRegistry {

    // sessionId -> userId
    private final Map<String, UUID> sessionToUser =
            new ConcurrentHashMap<>();

    // accountId -> userId
    private final Map<UUID, UUID> accountToUser =
            new ConcurrentHashMap<>();

    public void register(
            UUID accountId,
            String sessionId,
            UUID userId) {
        sessionToUser.put(sessionId, userId);
        accountToUser.put(accountId, userId);
    }

    public void removeSession(String sessionId) {
        UUID userId = sessionToUser.remove(sessionId);
        if (userId != null) {
            // удаляем все записи accountId -> userId для этой сессии
            accountToUser.entrySet()
                    .removeIf(entry -> entry.getValue().equals(userId));
        }
    }

    public Optional<UUID> getUserIdByAccountId(UUID accountId) {
        return Optional.ofNullable(accountToUser.get(accountId));
    }

    public Optional<UUID> getUserIdBySessionId(String sessionId) {
        return Optional.ofNullable(sessionToUser.get(sessionId));
    }
}