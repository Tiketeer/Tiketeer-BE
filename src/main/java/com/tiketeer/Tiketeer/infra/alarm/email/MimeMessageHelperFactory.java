package com.tiketeer.Tiketeer.infra.alarm.email;

import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public interface MimeMessageHelperFactory {
	MimeMessageHelper createMimeMessageHelper(MimeMessage message) throws MessagingException;
}
