package com.carelink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CarelinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarelinkApplication.class, args);
	}

}
