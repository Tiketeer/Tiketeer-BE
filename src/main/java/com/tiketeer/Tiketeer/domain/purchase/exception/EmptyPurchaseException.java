package com.tiketeer.Tiketeer.domain.purchase.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.PurchaseExceptionCode;

public class EmptyPurchaseException extends DefinedException {
	public EmptyPurchaseException() {
		super(PurchaseExceptionCode.EMPTY_PURCHASE);
	}
}
