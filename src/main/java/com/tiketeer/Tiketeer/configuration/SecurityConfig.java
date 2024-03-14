package com.tiketeer.Tiketeer.configuration;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		return http.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(req ->
				req.requestMatchers(getPermitAllPaths()).permitAll()
					.anyRequest()
					.authenticated()
			)
			.build();
	}

	private String[] getPermitAllPaths() {
		return Stream.concat(
			getSwaggerPaths().stream(),
			getMemberPaths().stream()
		).toList().toArray(String[]::new);
	}

	private List<String> getSwaggerPaths() {
		return List.of("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**");
	}

	private List<String> getMemberPaths() {
		return List.of("/login", "/members/register");
	}
}
