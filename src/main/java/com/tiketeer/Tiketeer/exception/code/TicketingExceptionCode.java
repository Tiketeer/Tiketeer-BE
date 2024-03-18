package com.tiketeer.Tiketeer.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketingExceptionCode implements ExceptionCode {
	TICKETING_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 티케팅입니다."),
	EVENT_TIME_NOT_VALID(HttpStatus.BAD_REQUEST, "유효하지 않은 이벤트 일시입니다."),
	SALE_DURATION_NOT_VALID(HttpStatus.BAD_REQUEST, "유효하지 않은 판매 기간입니다."),
	UPDATE_TICKETING_AFTER_SALE_START(HttpStatus.FORBIDDEN, "판매 기간이 시작된 후에는 티케팅을 수정할 수 없습니다."),
	DELETE_TICKETING_AFTER_SALE_START(HttpStatus.FORBIDDEN, "판매 기간이 시작된 후에는 티케팅을 삭제할 수 없습니다."),
	MODIFY_FOR_NOT_OWNED_TICKETING(HttpStatus.FORBIDDEN, "본인 소유가 아닌 티케팅을 수정, 삭제가 불가능합니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
