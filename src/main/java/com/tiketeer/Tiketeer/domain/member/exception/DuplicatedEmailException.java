package com.tiketeer.Tiketeer.domain.member.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.MemberExceptionCode;

public class DuplicatedEmailException extends DefinedException {
	public DuplicatedEmailException() {
		super(MemberExceptionCode.DUPLICATED_EMAIL);
	}
}
