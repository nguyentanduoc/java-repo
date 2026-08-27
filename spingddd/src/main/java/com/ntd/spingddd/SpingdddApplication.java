package com.ntd.spingddd;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
public class SpingdddApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpingdddApplication.class, args);
	}

	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
	}
}
