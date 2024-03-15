package com.tiketeer.Tiketeer.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketingExceptionCode implements ExceptionCode {
	TICKETING_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 티케팅입니다."),
	EVENT_TIME_NOT_VALID(HttpStatus.BAD_REQUEST, "유효하지 않은 이벤트 일시입니다."),
	SALE_DURATION_NOT_VALID(HttpStatus.BAD_REQUEST, "유효하지 않은 판매 기간입니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
