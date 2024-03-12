package com.tiketeer.Tiketeer.domain.member.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.MemberExceptionCode;

public class MemberNotFoundException extends DefinedException {
	public MemberNotFoundException() {
		super(MemberExceptionCode.MEMBER_NOT_FOUND);
	}
}
