package com.tiketeer.Tiketeer.domain.ticketing.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.TicketingExceptionCode;

public class DeleteTicketingAfterSaleStartException extends DefinedException {
	public DeleteTicketingAfterSaleStartException() {
		super(TicketingExceptionCode.DELETE_TICKETING_AFTER_SALE_START);
	}
}
