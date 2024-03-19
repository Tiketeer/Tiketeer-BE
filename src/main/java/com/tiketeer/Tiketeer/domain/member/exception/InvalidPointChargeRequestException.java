package com.tiketeer.Tiketeer.domain.member.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.MemberExceptionCode;

public class InvalidPointChargeRequestException extends DefinedException {
	public InvalidPointChargeRequestException() {
		super(MemberExceptionCode.INVALID_POINT_CHARGE_REQUEST);
	}
}
