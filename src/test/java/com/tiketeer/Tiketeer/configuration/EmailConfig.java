package com.tiketeer.Tiketeer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class EmailConfig {
	@Bean
	public JavaMailSender javaMailSender() {
		return new MockJavaMailSenderImpl();
	}
}
