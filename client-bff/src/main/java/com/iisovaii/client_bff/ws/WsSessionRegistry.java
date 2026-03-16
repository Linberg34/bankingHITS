package com.iisovaii.client_bff.ws;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Простейший реестр WebSocket‑сессий по пользователям.
 * Сейчас используется как инфраструктурный компонент, который можно
 * задействовать из слушателей событий (SessionConnect/Disconnect) или
 * Kafka‑consumer, если потребуется понимать, есть ли активные подписки
 * у конкретного пользователя.
 */
@Component
public class WsSessionRegistry {

    // userId -> sessionIds
    private final Map<String, Set<String>> sessionsByUser = new ConcurrentHashMap<>();

    public void registerSession(String userId, String sessionId) {
        sessionsByUser
                .computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet())
                .add(sessionId);
    }

    public void unregisterSession(String userId, String sessionId) {
        Set<String> sessions = sessionsByUser.get(userId);
        if (sessions == null) {
            return;
        }
        sessions.remove(sessionId);
        if (sessions.isEmpty()) {
            sessionsByUser.remove(userId);
        }
    }

    public Set<String> getSessionIds(String userId) {
        Set<String> sessions = sessionsByUser.get(userId);
        if (sessions == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(sessions);
    }
}

