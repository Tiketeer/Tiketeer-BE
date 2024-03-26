package com.tiketeer.Tiketeer.infra.alarm.email.view;

import java.util.UUID;

import org.springframework.web.util.UriComponentsBuilder;

import lombok.Builder;

public class AuthenticateEmailViewStrategy implements EmailViewStrategy {
	private final String email;
	private final UUID otp;
	private final String baseUrl;

	@Builder
	public AuthenticateEmailViewStrategy(String email, UUID otp, String baseUrl) {
		this.email = email;
		this.otp = otp;
		this.baseUrl = baseUrl;
	}

	@Override
	public String createView() {
		var url =
			UriComponentsBuilder.fromHttpUrl(baseUrl)
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
