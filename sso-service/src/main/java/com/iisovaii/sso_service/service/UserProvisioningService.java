package com.iisovaii.sso_service.service;

import com.iisovaii.sso_service.domain.Role;
import com.iisovaii.sso_service.dto.UserProfileCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProvisioningService {

    private final RestClient.Builder restClientBuilder;

    @Value("${services.user-service-url:http://localhost:8081}")
    private String userServiceUrl;

    @Value("${sso.user-provisioning.max-attempts:10}")
    private int maxAttempts;

    @Value("${sso.user-provisioning.retry-delay-ms:2000}")
    private long retryDelayMs;

    public void ensureUserProfile(
            String name,
            String email,
            List<Role> roles) {

        RestClient restClient = restClientBuilder
                .baseUrl(userServiceUrl)
                .build();

        UserProfileCreateRequest request = new UserProfileCreateRequest(
                name,
                email
        );

        String uri = resolveRegistrationUri(roles);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                restClient.post()
                        .uri(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .toBodilessEntity();

                log.info("User profile {} ensured in user-service", email);
                return;
            } catch (HttpClientErrorException.Conflict exception) {
                log.info(
                        "User profile {} already exists in user-service",
                        email
                );
                return;
            } catch (RestClientException exception) {
                if (attempt == maxAttempts) {
                    throw new IllegalStateException(
                            "Failed to provision user profile " + email,
                            exception
                    );
                }

                log.warn(
                        "User provisioning attempt {}/{} failed for {}: {}",
                        attempt,
                        maxAttempts,
                        email,
                        exception.getMessage()
                );
                sleepBeforeRetry();
            }
        }
    }

    private String resolveRegistrationUri(List<Role> roles) {
        if (roles != null && roles.contains(Role.EMPLOYEE)) {
            return "/api/users/employees";
        }
        return "/api/users/clients";
    }

    private void sleepBeforeRetry() {
        try {
            Thread.sleep(retryDelayMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrupted while waiting to retry user provisioning",
                    exception
            );
        }
    }
}
