package com.gautama.bankhitscredit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class CoreServiceClient {

    private final RestTemplate restTemplate;
    private final String coreUrl;

    public CoreServiceClient(@Value("${core-service.url}") String coreUrl) {
        this.restTemplate = new RestTemplate();
        this.coreUrl = coreUrl;
    }

    /**
     * Зачислить деньги на счёт (при выдаче кредита)
     */
    public void deposit(Long accountId, BigDecimal amount) {
        String url = coreUrl + "/api/accounts/{accountId}/deposit";
        Map<String, Object> body = Map.of("amount", amount);
        restTemplate.postForEntity(
                url,
                new HttpEntity<>(body, jsonHeaders()),
                Void.class,
                accountId
        );
    }

    /**
     * Списать деньги со счёта (периодический платёж по кредиту)
     * Возвращает true если списание прошло успешно
     */
    public boolean withdraw(Long accountId, BigDecimal amount) {
        try {
            String url = coreUrl + "/api/accounts/{accountId}/withdraw";
            Map<String, Object> body = Map.of("amount", amount);
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    url,
                    new HttpEntity<>(body, jsonHeaders()),
                    Void.class,
                    accountId
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            // Ядро вернуло ошибку (например, недостаточно средств)
            return false;
        }
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}