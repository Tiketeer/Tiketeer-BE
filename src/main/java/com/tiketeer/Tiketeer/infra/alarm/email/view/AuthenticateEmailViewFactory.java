package com.tiketeer.Tiketeer.infra.alarm.email.view;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.tiketeer.Tiketeer.infra.alarm.email.view.dto.CreateEmailViewCommandDto;

@Component
public class AuthenticateEmailViewFactory implements EmailViewFactory {
	@Value("${custom.service.baseUrl}")
	private String baseUrl;

	@Override
	public String createView(Object data) {
		CreateEmailViewCommandDto authenticateEmailViewData = (CreateEmailViewCommandDto)data;
		var url =
			UriComponentsBuilder.fromHttpUrl(baseUrl)
				.path("/confirm/email")
				.queryParam("otp", authenticateEmailViewData.getOtp())
				.queryParam("email", authenticateEmailViewData.getEmail())
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
