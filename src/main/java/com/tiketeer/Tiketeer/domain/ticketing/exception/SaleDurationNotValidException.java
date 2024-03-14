package com.tiketeer.Tiketeer.domain.ticketing.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.TicketingExceptionCode;

public class SaleDurationNotValidException extends DefinedException {
	public SaleDurationNotValidException() {
		super(TicketingExceptionCode.SALE_DURATION_NOT_VALID);
	}
}
