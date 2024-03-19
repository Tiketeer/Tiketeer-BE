package com.tiketeer.Tiketeer.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PurchaseExceptionCode implements ExceptionCode {
	PURCHASE_NOT_IN_SALE_PERIOD(HttpStatus.BAD_REQUEST, "티켓 판매 기간이 아닙니다."),

	NOT_ENOUGH_TICKET(HttpStatus.CONFLICT, "구매 가능한 티켓이 부족합니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
