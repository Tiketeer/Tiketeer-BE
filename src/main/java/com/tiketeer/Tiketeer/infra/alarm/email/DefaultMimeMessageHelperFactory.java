package com.tiketeer.Tiketeer.infra.alarm.email;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class DefaultMimeMessageHelperFactory implements MimeMessageHelperFactory {
	@Override
	public MimeMessageHelper createMimeMessageHelper(MimeMessage message) throws MessagingException {
		return new MimeMessageHelper(message, true);
	}
}
