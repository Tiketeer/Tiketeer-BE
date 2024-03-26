package com.tiketeer.Tiketeer.infra.alarm.email;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.javamail.JavaMailSender;

import com.tiketeer.Tiketeer.infra.alarm.email.view.AuthenticateEmailViewStrategy;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
	@Autowired
	private EmailService emailService;
	@SpyBean
	private JavaMailSender emailSender;

	@Test
	@DisplayName("상대 메일, 제목, 내용 입력 > 메일 전송 요청 > 내부 send 메서드 호출")
	void sendEmailSuccessWithText() throws MessagingException {
		emailService.sendEmail("tiketest@gmail.com", "test1", "<!DOCTYPE html>\n"
			+ "<html>\n"
			+ "<head>\n"
			+ "    <title>POST 요청 예제</title>\n"
			+ "</head>\n"
			+ "<body>\n"
			+ "\n"
			+ "<button id=\"sendPostBtn\">POST 요청 보내기</button\n"
			+ "</body>\n"
			+ "</html>");

		Mockito.verify(emailSender).send(ArgumentMatchers.any(MimeMessage.class));
	}

	@Test
	@DisplayName("상대 메일, 제목, 뷰 전략 입력 > 메일 전송 요청 > 내부 send 메서드 호출")
	void sendEmailSuccessWithViewStrategy() throws MessagingException {
		// given
		var viewStrategy = AuthenticateEmailViewStrategy.builder()
			.email("tiketest@gmail.com")
			.otp(UUID.randomUUID())
			.baseUrl("http://mockUrl:9999")
			.build();

		// when
		emailService.sendEmail("tiketest@gmail.com", "test1", viewStrategy);

		// then
		Mockito.verify(emailSender).send(ArgumentMatchers.any(MimeMessage.class));
	}
}