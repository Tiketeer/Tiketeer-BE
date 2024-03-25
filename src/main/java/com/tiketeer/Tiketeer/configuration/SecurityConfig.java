package com.tiketeer.Tiketeer.configuration;

import static com.tiketeer.Tiketeer.domain.role.constant.RoleEnum.*;
import static org.springframework.http.HttpMethod.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.tiketeer.Tiketeer.auth.constant.PublicPaths;
import com.tiketeer.Tiketeer.auth.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(req ->
				req.requestMatchers(PublicPaths.getSwaggerPaths().toArray(String[]::new)).permitAll()
			);

		configureTicketingSecurity(http);
		configureMemberSecurity(http);
		configureAuthSecurity(http);
		configurePurchaseSecurity(http);

		return http.build();

	}

	private void configureTicketingSecurity(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(req ->
			req.requestMatchers(POST, "/ticketings").hasAuthority(SELLER.name())
				.requestMatchers(PATCH, "/ticketings/*").hasAuthority(SELLER.name())
				.requestMatchers(DELETE, "/ticketings/*").hasAuthority(SELLER.name())
				.requestMatchers(GET, "/ticketings").permitAll()
				.requestMatchers(GET, "/ticketings/*").permitAll()
		);
	}

	private void configureMemberSecurity(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(req ->
			req
				.requestMatchers(POST, "/members/register").permitAll()
				.requestMatchers(POST, "/members/password-reset/mail").hasAnyAuthority(BUYER.name(), SELLER.name())
				.requestMatchers(PUT, "/members/password").hasAnyAuthority(BUYER.name(), SELLER.name())
				.requestMatchers(DELETE, "/members/*").hasAnyAuthority(BUYER.name(), SELLER.name())
				.requestMatchers(GET, "/members/*").hasAnyAuthority(BUYER.name(), SELLER.name())
				.requestMatchers(GET, "/members/*/purchases").hasAnyAuthority(BUYER.name(), SELLER.name())
				.requestMatchers(GET, "/members/*/sale").hasAuthority(SELLER.name())
				.requestMatchers(POST, "/members/*/points").hasAnyAuthority(BUYER.name(), SELLER.name())
		);
	}

	private void configureAuthSecurity(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(req ->
			req
				.requestMatchers(POST, "/auth/login").permitAll()
				.requestMatchers(POST, "/auth/refresh").permitAll()
		);
	}

	private void configurePurchaseSecurity(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(req ->
			req.requestMatchers(POST, "/purchases").hasAnyAuthority(BUYER.name(), SELLER.name())
				.requestMatchers(DELETE, "/purchases/*/tickets").hasAnyAuthority(BUYER.name(), SELLER.name())
		);
	}

}
