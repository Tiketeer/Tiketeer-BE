package com.tiketeer.Tiketeer.domain.member.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.MemberExceptionCode;

public class NonFulfilledEventExistException extends DefinedException {
	public NonFulfilledEventExistException() {
		super(MemberExceptionCode.NON_FULFILLMENT_EVENT_EXIST);
	}
}
