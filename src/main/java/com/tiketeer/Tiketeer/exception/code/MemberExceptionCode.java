package com.tiketeer.Tiketeer.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberExceptionCode implements ExceptionCode {
	DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 멤버입니다."),
	INVALID_OTP(HttpStatus.BAD_REQUEST, "유효하지 않은 Otp입니다."),
	INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "유효하지 않은 회원 정보입니다"),
	MEMBER_ID_AND_AUTH_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "인증 정보와 요청 유저 ID가 매칭되지 않습니다."),
	INVALID_POINT_CHARGE_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 포인트 충전 요청입니다."),
	INVALID_NEW_PASSWORD(HttpStatus.CONFLICT, "동일한 비밀번호로 변경할 수 없습니다"),
	NON_FULFILLMENT_EVENT_EXIST(HttpStatus.FORBIDDEN, "등록한 이벤트 중 종료되지 않은 이벤트가 존재합니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
