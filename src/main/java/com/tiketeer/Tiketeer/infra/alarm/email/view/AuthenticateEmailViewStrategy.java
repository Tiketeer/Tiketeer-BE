package com.tiketeer.Tiketeer.infra.alarm.email.view;

import java.util.UUID;

import org.springframework.web.util.UriComponentsBuilder;

import lombok.Builder;

public class AuthenticateEmailViewStrategy implements EmailViewStrategy {
	private final String email;
	private final UUID otp;
	private final String baseUrl;
	private final String port;

	@Builder
	public AuthenticateEmailViewStrategy(String email, UUID otp, String baseUrl, String port) {
		this.email = email;
		this.otp = otp;
		this.baseUrl = baseUrl;
		this.port = port;
	}

	@Override
	public String createView() {
		var url =
			UriComponentsBuilder.fromHttpUrl(baseUrl)
				.port(port)
				.path("/confirm/email")
				.queryParam("otp", otp)
				.queryParam("email", email)
				.build();

		return "<!DOCTYPE html>"
			+ "<html>"
			+ "<body>"
			+ "<div>"
			+ "<a href=\"" + url.toUriString() + "\"> 인증하기" + "</a>"
			+ "</div>"
			+ "</body>"
			+ "</html>";
	}
}
