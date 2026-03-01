package com.gautama.bankhitsuser;

import com.gautama.bankhitsuser.client.AccountServiceClient;
import com.gautama.bankhitsuser.client.CreditServiceClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.gautama.bankhitsuser.model")
@EnableJpaRepositories(basePackages = "com.gautama.bankhitsuser.repository")
@EnableFeignClients(clients = {AccountServiceClient.class, CreditServiceClient.class})
public class BankHitsUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankHitsUserApplication.class, args);
    }

}
