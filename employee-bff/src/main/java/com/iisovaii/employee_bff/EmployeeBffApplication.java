package com.iisovaii.employee_bff;

import com.iisovaii.employee_bff.client.AccountServiceClient;
import com.iisovaii.employee_bff.client.CreditServiceClient;
import com.iisovaii.employee_bff.client.SsoServiceClient;
import com.iisovaii.employee_bff.client.UserServiceClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {
		AccountServiceClient.class,
		CreditServiceClient.class,
		UserServiceClient.class,
		SsoServiceClient.class
})
public class EmployeeBffApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeBffApplication.class, args);
	}

}
