package com.tiketeer.Tiketeer.domain.member.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.AuthExceptionCode;

public class InvalidTokenException extends DefinedException {
	public InvalidTokenException() {
		super(AuthExceptionCode.NEED_LOGIN);
	}
}
