package com.iisovaii.client_bff.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.iisovaii.client_bff.domain.FcmToken;
import com.iisovaii.client_bff.repository.FcmTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FcmNotificationService {

    private final FcmTokenRepository tokenRepository;
    private final boolean enabled;

    public FcmNotificationService(
            FcmTokenRepository tokenRepository,
            @Value("${firebase.service-account-path:}") String serviceAccountPath
    ) {
        this.tokenRepository = tokenRepository;
        this.enabled = StringUtils.hasText(serviceAccountPath) && initFirebase(serviceAccountPath);
        if (!enabled) {
            log.warn("Firebase не настроен — push-уведомления отключены. " +
                    "Укажите firebase.service-account-path в application.properties");
        }
    }

    private boolean initFirebase(String path) {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                try (InputStream is = new FileInputStream(path)) {
                    NetHttpTransport transport = new NetHttpTransport.Builder()
                            .doNotValidateCertificate()
                            .build();
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(is))
                            .setHttpTransport(transport)
                            .build();
                    FirebaseApp.initializeApp(options);
                }
            }
            log.info("Firebase инициализирован");
            return true;
        } catch (Exception e) {
            log.warn("Ошибка инициализации Firebase: {}", e.getMessage());
            return false;
        }
    }

    @Transactional
    public void saveToken(UUID userId, String token) {
        tokenRepository.findByToken(token).ifPresentOrElse(
                existing -> {
                    if (!existing.getUserId().equals(userId)) {
                        existing.setUserId(userId);
                    }
                },
                () -> tokenRepository.save(new FcmToken(userId, token))
        );
    }

    @Transactional
    public void removeToken(String token) {
        tokenRepository.deleteByToken(token);
    }

    public List<String> getTokensForUser(UUID userId) {
        return tokenRepository.findByUserId(userId)
                .stream()
                .map(FcmToken::getToken)
                .toList();
    }

    public void sendToUser(UUID userId, String title, String body) {
        if (!enabled) return;
        List<String> tokens = tokenRepository.findByUserId(userId)
                .stream()
                .map(FcmToken::getToken)
                .toList();
        if (tokens.isEmpty()) return;
        send(tokens, title, body);
    }

    private void send(List<String> tokens, String title, String body) {
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .addAllTokens(tokens)
                .build();
        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("FCM: отправлено {}, ошибок {}", response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.warn("Ошибка отправки FCM: {}", e.getMessage());
        }
    }
}
