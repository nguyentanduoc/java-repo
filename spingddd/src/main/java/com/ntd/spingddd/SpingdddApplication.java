package com.ntd.spingddd;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
@EnableScheduling
@RequiredArgsConstructor
public class SpingdddApplication {

	private final Environment env;

	public static void main(String[] args) {
		SpringApplication.run(SpingdddApplication.class, args);
	}

	@PostConstruct
	void started() {
		// Skip timezone setting for tests to avoid Postgres TimeZone issues
		if (java.util.Arrays.asList(env.getActiveProfiles()).contains("test")) {
			return;
		}
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
	}
}
