package com.tiketeer.Tiketeer.domain.ticketing.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.TicketingExceptionCode;

public class ModifyForNotOwnedTicketingException extends DefinedException {
	public ModifyForNotOwnedTicketingException() {
		super(TicketingExceptionCode.MODIFY_FOR_NOT_OWNED_TICKETING);
	}
}
