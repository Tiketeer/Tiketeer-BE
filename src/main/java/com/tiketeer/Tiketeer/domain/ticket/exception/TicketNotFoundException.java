package com.tiketeer.Tiketeer.domain.ticket.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.TicketExceptionCode;

public class TicketNotFoundException extends DefinedException {
	public TicketNotFoundException() {
		super(TicketExceptionCode.TICKET_NOT_FOUND);
	}
}
