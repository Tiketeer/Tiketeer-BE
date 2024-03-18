package com.tiketeer.Tiketeer.domain.member.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.MemberExceptionCode;

public class MemberIdAndAuthNotMatchedException extends DefinedException {
	public MemberIdAndAuthNotMatchedException() {
		super(MemberExceptionCode.MEMBER_ID_AND_AUTH_NOT_MATCHED);
	}
}
