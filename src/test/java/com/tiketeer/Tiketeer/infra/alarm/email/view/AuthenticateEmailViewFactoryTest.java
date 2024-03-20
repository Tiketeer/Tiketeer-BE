package com.tiketeer.Tiketeer.infra.alarm.email.view;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.tiketeer.Tiketeer.domain.member.service.dto.CreateEmailViewCommandDto;

@SpringBootTest
class AuthenticateEmailViewFactoryTest {
	private final EmailViewFactory emailViewFactory;
	@Value("${custom.service.baseUrl}")
	private String baseUrl;

	@Autowired
	AuthenticateEmailViewFactoryTest(EmailViewFactory emailViewFactory) {
		this.emailViewFactory = emailViewFactory;
	}

	@Test
	@DisplayName("메일 정보 > 뷰 생성 > 생성된 뷰 비교")
	void createView() {
		// given
		CreateEmailViewCommandDto createEmailViewCommandDto = new CreateEmailViewCommandDto(UUID.randomUUID(),
			"test@gmail.com");

		// when
		String result = emailViewFactory.createView(createEmailViewCommandDto);

		// then
		var url =
			baseUrl + ":4080/confirm/email?otp=" + createEmailViewCommandDto.getOtp() + "&email="
				+ createEmailViewCommandDto.getEmail();

		var expected = "<!DOCTYPE html>"
			+ "<html>"
			+ "<body>"
			+ "<div>"
			+ "<a href=\"" + url + "\"> 인증하기" + "</a>"
			+ "</div>"
			+ "</body>"
			+ "</html>";

		Assertions.assertThat(Arrays.stream(result.split("\n")).collect(Collectors.joining(" "))).isEqualTo(
			Arrays.stream(expected.split("\n")).collect(Collectors.joining(" ")));
	}
}