package com.koroFoods.userServiceSoap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceSoapApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceSoapApplication.class, args);
	}

}
