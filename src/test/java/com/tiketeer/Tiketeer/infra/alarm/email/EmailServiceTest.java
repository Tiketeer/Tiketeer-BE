package com.tiketeer.Tiketeer.infra.alarm.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.javamail.JavaMailSender;

import com.tiketeer.Tiketeer.infra.email.EmailService;

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
	void sendEmail() throws MessagingException {
		emailService.sendEmail("gns8167@naver.com", "test1", "<!DOCTYPE html>\n"
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
}