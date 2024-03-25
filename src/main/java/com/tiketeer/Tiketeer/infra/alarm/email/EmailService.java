package com.tiketeer.Tiketeer.infra.alarm.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.tiketeer.Tiketeer.infra.alarm.email.exception.MessagingRuntimeException;
import com.tiketeer.Tiketeer.infra.alarm.email.view.EmailViewStrategy;

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

	public void sendEmail(String toEmail, String title, String text) {
		MimeMessage message = emailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = helperFactory.createMimeMessageHelper(message);
			helper.setTo(toEmail);
			helper.setSubject(title);

			helper.setText(text, true);
		} catch (MessagingException ex) {
			throw new MessagingRuntimeException(toEmail, ex.getMessage());
		}

		emailSender.send(message);
	}

	public void sendEmail(String toEmail, String title, EmailViewStrategy emailViewStrategy) {
		sendEmail(toEmail, title, emailViewStrategy.createView());
	}
}
