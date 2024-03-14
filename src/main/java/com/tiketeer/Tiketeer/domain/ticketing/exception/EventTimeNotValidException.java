package com.tiketeer.Tiketeer.domain.ticketing.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.TicketingExceptionCode;

public class EventTimeNotValidException extends DefinedException {
	public EventTimeNotValidException() {
		super(TicketingExceptionCode.EVENT_TIME_NOT_VALID);
	}
}
