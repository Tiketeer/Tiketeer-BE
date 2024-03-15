package com.tiketeer.Tiketeer.infra.email.exception;

public class MessagingRuntimeException extends RuntimeException {
	public MessagingRuntimeException(String target, String message) {
		super("Mail sending failure: [" + target + "] - " + message);
	}
}
