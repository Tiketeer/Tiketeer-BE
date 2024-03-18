package com.tiketeer.Tiketeer.infra.alarm.email.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.EmailExceptionCode;

public class MessagingRuntimeException extends DefinedException {
	public MessagingRuntimeException(String target, String message) {
		super(EmailExceptionCode.CREATE_MAIL_HELPER_FAIL, "Mail sending failure: [" + target + "] - " + message);
	}
}
