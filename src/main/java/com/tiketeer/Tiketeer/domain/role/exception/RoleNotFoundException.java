package com.tiketeer.Tiketeer.domain.role.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.RoleExceptionCode;

public class RoleNotFoundException extends DefinedException {
	public RoleNotFoundException() {
		super(RoleExceptionCode.ROLE_NOT_FOUND);
	}
}
