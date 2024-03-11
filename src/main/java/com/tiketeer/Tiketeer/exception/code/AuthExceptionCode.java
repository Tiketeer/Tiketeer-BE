package com.tiketeer.Tiketeer.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionCode implements ExceptionCode {
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다. 재로그인이 필요합니다."),
	WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 패스워드입니다. 다시 시도해주십시오."),
	INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, "작업을 수행할 권한이 부족합니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
