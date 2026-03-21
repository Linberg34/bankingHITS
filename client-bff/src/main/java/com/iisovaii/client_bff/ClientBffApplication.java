package com.iisovaii.client_bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.iisovaii.client_bff.client")
public class ClientBffApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientBffApplication.class, args);
	}

}
