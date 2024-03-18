package com.tiketeer.Tiketeer.configuration;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import jakarta.mail.internet.MimeMessage;

public class MockJavaMailSenderImpl extends JavaMailSenderImpl {
	@Override
	public void send(MimeMessage message) {
		return;
	}
}
