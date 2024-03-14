package com.tiketeer.Tiketeer.domain.ticketing.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.TicketingExceptionCode;

public class SaleDurationNotValidException extends DefinedException {
	public SaleDurationNotValidException() {
		super(TicketingExceptionCode.EVENT_TIME_NOT_VALID);
	}
}
