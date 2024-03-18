package com.tiketeer.Tiketeer.infra.alarm.email.view;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.tiketeer.Tiketeer.domain.member.service.dto.CreateEmailViewCommandDto;

@Component
public class AuthenticateEmailViewFactory implements EmailViewFactory {
	@Value("${custom.service.baseUrl}")
	private String baseUrl;

	@Value("${server.port}")
	private String port;

	@Override
	public String createView(Object data) {
		CreateEmailViewCommandDto authenticateEmailViewData = (CreateEmailViewCommandDto)data;
		System.out.println(baseUrl);
		var url =
			UriComponentsBuilder.fromHttpUrl(baseUrl)
				.port(port)
				.path("/confirm/email")
				.queryParam("otp", authenticateEmailViewData.getOtp())
				.queryParam("email", authenticateEmailViewData.getEmail())
				.build();

		return "<!DOCTYPE html>\n"
			+ "<html>\n"
			+ "<body>\n"
			+ "\n"
			+ "<a>" + url + "</a>\n"
			+ "</body>\n"
			+ "</html>";
	}
}
