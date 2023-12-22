package com.likhith.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class GatewayServiceFlipkartDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceFlipkartDemoApplication.class, args);
	}

}