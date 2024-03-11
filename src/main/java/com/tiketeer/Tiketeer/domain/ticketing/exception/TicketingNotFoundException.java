package com.tiketeer.Tiketeer.domain.ticketing.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.TicketingExceptionCode;

public class TicketingNotFoundException extends DefinedException {
	public TicketingNotFoundException() {
		super(TicketingExceptionCode.TICKETING_NOT_FOUND);
	}
}
