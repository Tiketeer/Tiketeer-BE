package com.tiketeer.Tiketeer.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleExceptionCode implements ExceptionCode {
	ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 롤입니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
