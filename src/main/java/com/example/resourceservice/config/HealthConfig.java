package com.example.resourceservice.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.DefaultHealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class HealthConfig {

	private final Map<String, HealthContributor> healthContributors;

	@Bean
	HealthContributorRegistry fullHealthRegistry() {
		return new DefaultHealthContributorRegistry(
				new LinkedHashMap<>(healthContributors));
	}

}
