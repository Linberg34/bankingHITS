package com.iisovaii.sso_service.service;

import com.iisovaii.sso_service.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataInitializer implements ApplicationRunner {

    private final AuthService authService;

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Value("${app.auth.default-password:password}")
    private String defaultPassword;

    @Value("${app.seed.employee.name:Default Employee}")
    private String employeeName;

    @Value("${app.seed.employee.email:employee@bank.local}")
    private String employeeEmail;

    @Value("${app.seed.client.name:Default Client}")
    private String clientName;

    @Value("${app.seed.client.email:client@bank.local}")
    private String clientEmail;

    @Override
    public void run(ApplicationArguments args) {
        if (!seedEnabled) {
            return;
        }

        authService.ensureRegistered(
                employeeName,
                employeeEmail,
                defaultPassword,
                List.of(Role.EMPLOYEE)
        );

        authService.ensureRegistered(
                clientName,
                clientEmail,
                defaultPassword,
                List.of(Role.CLIENT)
        );

        log.info("SSO seed initialization completed");
    }
}
