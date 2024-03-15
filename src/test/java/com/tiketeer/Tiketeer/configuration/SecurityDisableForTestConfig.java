package com.tiketeer.Tiketeer.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class SecurityDisableForTestConfig {
	@Bean
	public SecurityFilterChain disableFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).httpBasic(c -> c.disable());
		return http.build();
	}
}
