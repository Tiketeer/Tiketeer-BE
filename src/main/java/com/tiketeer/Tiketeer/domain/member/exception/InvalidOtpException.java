package com.tiketeer.Tiketeer.domain.member.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.MemberExceptionCode;

public class InvalidOtpException extends DefinedException {
	public InvalidOtpException() {
		super(MemberExceptionCode.INVALID_OTP);
	}
}
