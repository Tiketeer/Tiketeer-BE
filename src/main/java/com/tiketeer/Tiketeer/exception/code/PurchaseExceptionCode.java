package com.tiketeer.Tiketeer.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PurchaseExceptionCode implements ExceptionCode {
	PURCHASE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 구매 내역입니다."),
	EMPTY_PURCHASE(HttpStatus.CONFLICT, "구매 내역에 티켓이 존재하지 않습니다."),
	PURCHASE_NOT_IN_SALE_PERIOD(HttpStatus.BAD_REQUEST, "티켓 판매 기간이 아닙니다."),

	NOT_ENOUGH_TICKET(HttpStatus.CONFLICT, "구매 가능한 티켓이 부족합니다."),
	ACCESS_FOR_NOT_OWNED_PURCHASE(HttpStatus.FORBIDDEN, "본인 소유가 아닌 구매 내역에 접근 불가능합니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
