package com.gautama.bankhitsuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.gautama.bankhitsuser.model")
@EnableJpaRepositories(basePackages = "com.gautama.bankhitsuser.repository")
public class BankHitsUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankHitsUserApplication.class, args);
    }

}
