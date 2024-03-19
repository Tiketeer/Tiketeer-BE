package com.tiketeer.Tiketeer.domain.purchase.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.PurchaseExceptionCode;

public class NotEnoughTicketException extends DefinedException {
	public NotEnoughTicketException() {
		super(PurchaseExceptionCode.NOT_ENOUGH_TICKET);
	}
}
