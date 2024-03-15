package com.tiketeer.Tiketeer.infra.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	private final JavaMailSender emailSender;
	private final MimeMessageHelperFactory helperFactory;

	@Autowired
	public EmailService(JavaMailSender emailSender, MimeMessageHelperFactory helperFactory) {
		this.emailSender = emailSender;
		this.helperFactory = helperFactory;
	}

	public void sendEmail(String toEmail, String title, String text) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();

		MimeMessageHelper helper = helperFactory.createMimeMessageHelper(message);
		helper.setTo(toEmail);
		helper.setSubject(title);

		helper.setText(text, true);

		emailSender.send(message);
	}
}
