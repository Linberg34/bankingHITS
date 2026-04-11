package com.iisovaii.sso_service.service;

import com.iisovaii.sso_service.domain.Role;
import com.iisovaii.sso_service.dto.UserProfileCreateRequest;
import com.iisovaii.sso_service.infrastructure.resilience.DownstreamCallExecutor;
import com.iisovaii.sso_service.infrastructure.trace.TraceContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProvisioningService {

    private final RestClient.Builder restClientBuilder;
    private final DownstreamCallExecutor downstreamCallExecutor;

    @org.springframework.beans.factory.annotation.Value("${services.user-service-url:http://localhost:8081}")
    private String userServiceUrl;

    public void ensureUserProfile(
            UUID id,
            String name,
            String email,
            List<Role> roles) {

        RestClient restClient = restClientBuilder
                .baseUrl(userServiceUrl)
                .build();

        UserProfileCreateRequest request = new UserProfileCreateRequest(
                id,
                name,
                email
        );

        String uri = resolveRegistrationUri(roles);

        downstreamCallExecutor.execute(
                DownstreamCallExecutor.DownstreamService.USERS,
                "POST",
                uri,
                201,
                () -> {
                    try {
                        restClient.post()
                                .uri(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(headers -> {
                                    headers.set(TraceContextHolder.TRACE_HEADER, TraceContextHolder.traceId());
                                    headers.set(TraceContextHolder.APP_HEADER, TraceContextHolder.appSource());

                                    String idempotencyKey = currentRequestHeader(TraceContextHolder.IDEMPOTENCY_HEADER);
                                    if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                                        headers.set(TraceContextHolder.IDEMPOTENCY_HEADER, idempotencyKey);
                                    }
                                })
                                .body(request)
                                .retrieve()
                                .toBodilessEntity();
                    } catch (RestClientResponseException exception) {
                        if (exception.getStatusCode().value() == 409) {
                            log.info("User profile {} already exists in user-service", email);
                            return null;
                        }
                        throw exception;
                    }
                    return null;
                }
        );

        log.info("User profile {} ensured in user-service", email);
    }

    private String resolveRegistrationUri(List<Role> roles) {
        if (roles != null && roles.contains(Role.EMPLOYEE)) {
            return "/api/users/employees";
        }
        return "/api/users/clients";
    }
    private String currentRequestHeader(String headerName) {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        return request != null ? request.getHeader(headerName) : null;
    }
}
