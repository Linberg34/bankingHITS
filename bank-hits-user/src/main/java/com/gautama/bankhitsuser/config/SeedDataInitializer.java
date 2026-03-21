//package com.gautama.bankhitsuser.config;
//
//import com.gautama.bankhitsuser.client.AccountServiceClient;
//import com.gautama.bankhitsuser.dto.AccountDTO;
//import com.gautama.bankhitsuser.enums.Role;
//import com.gautama.bankhitsuser.enums.Status;
//import com.gautama.bankhitsuser.model.User;
//import com.gautama.bankhitsuser.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class SeedDataInitializer implements ApplicationRunner {
//    private static final int ACCOUNT_CREATION_ATTEMPTS = 20;
//    private static final long ACCOUNT_CREATION_RETRY_DELAY_MS = 3000L;
//
//    private final UserRepository userRepository;
//    private final AccountServiceClient accountServiceClient;
//    private final PasswordEncoder passwordEncoder;
//
//    @Value("${app.seed.enabled:false}")
//    private boolean seedEnabled;
//
//    @Value("${app.seed.employee.name:Default Employee}")
//    private String employeeName;
//
//    @Value("${app.seed.employee.email:employee@bank.local}")
//    private String employeeEmail;
//
//    @Value("${app.seed.client.name:Default Client}")
//    private String clientName;
//
//    @Value("${app.seed.client.email:client@bank.local}")
//    private String clientEmail;
//
//    @Value("${app.seed.client.account-currency:RUB}")
//    private String clientAccountCurrency;
//
//    @Value("${app.auth.default-password:password}")
//    private String defaultPassword;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        if (!seedEnabled) {
//            return;
//        }
//
//        User employee = ensureUser(employeeName, employeeEmail, Role.EMPLOYEE);
//        User client = ensureUser(clientName, clientEmail, Role.CLIENT);
//
//        log.info("Seed users ready: employeeId={}, clientId={}", employee.getId(), client.getId());
//        ensureClientAccount(client);
//    }
//
//    private User ensureUser(String name, String email, Role role) {
//        return userRepository.findByEmail(email)
//                .orElseGet(() -> {
//                    User user = new User();
//                    user.setName(name);
//                    user.setEmail(email);
//                    user.setRole(role);
//                    user.setStatus(Status.ACTIVE);
//                    user.setPassword(passwordEncoder.encode(defaultPassword));
//                    return userRepository.save(user);
//                });
//    }
//
//    private void ensureClientAccount(User client) throws InterruptedException {
//        for (int attempt = 1; attempt <= ACCOUNT_CREATION_ATTEMPTS; attempt++) {
//            try {
//                List<AccountDTO> existingAccounts = accountServiceClient.getAccountsByUser(client.getId());
//                if (!existingAccounts.isEmpty()) {
//                    log.info("Client {} already has {} account(s)", client.getEmail(), existingAccounts.size());
//                    return;
//                }
//
//                AccountDTO createdAccount = accountServiceClient.createAccountCurrent(client.getId(), clientAccountCurrency);
//                log.info("Seed account created for client {}: {}", client.getEmail(), createdAccount.getAccountNumber());
//                return;
//            } catch (Exception exception) {
//                if (attempt == ACCOUNT_CREATION_ATTEMPTS) {
//                    throw exception;
//                }
//
//                log.warn("Unable to create seed account on attempt {}/{}. Retrying in {} ms",
//                        attempt, ACCOUNT_CREATION_ATTEMPTS, ACCOUNT_CREATION_RETRY_DELAY_MS);
//                Thread.sleep(ACCOUNT_CREATION_RETRY_DELAY_MS);
//            }
//        }
//    }
//}
