package com.example.iam_service2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class IamService2Application {

	public static void main(String[] args) {
		SpringApplication.run(IamService2Application.class, args);
	}
}
