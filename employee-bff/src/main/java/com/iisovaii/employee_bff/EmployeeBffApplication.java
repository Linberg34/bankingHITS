package com.iisovaii.employee_bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.iisovaii.employee_bff.client")
public class EmployeeBffApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeBffApplication.class, args);
	}

}
