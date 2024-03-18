package com.tiketeer.Tiketeer.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailExceptionCode implements ExceptionCode {
	CREATE_MAIL_HELPER_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "메일 생성에 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
