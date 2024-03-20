package com.tiketeer.Tiketeer.domain.member.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.MemberExceptionCode;

public class InvalidLoginException extends DefinedException {
	public InvalidLoginException() {
		super(MemberExceptionCode.INVALID_LOGIN);
	}
}
