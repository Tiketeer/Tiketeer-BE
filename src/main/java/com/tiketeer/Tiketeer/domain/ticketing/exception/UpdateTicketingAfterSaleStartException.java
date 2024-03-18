package com.tiketeer.Tiketeer.domain.ticketing.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.TicketingExceptionCode;

public class UpdateTicketingAfterSaleStartException extends DefinedException {
	public UpdateTicketingAfterSaleStartException() {
		super(TicketingExceptionCode.UPDATE_TICKETING_AFTER_SALE_START);
	}
}
