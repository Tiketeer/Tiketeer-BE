package com.tiketeer.Tiketeer.domain.member.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.MemberExceptionCode;

public class InvalidNewPasswordException extends DefinedException {
	public InvalidNewPasswordException() {
		super(MemberExceptionCode.INVALID_NEW_PASSWORD);
	}
}

