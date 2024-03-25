package com.tiketeer.Tiketeer.domain.purchase.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.PurchaseExceptionCode;

public class AccessForNotOwnedPurchaseException extends DefinedException {
	public AccessForNotOwnedPurchaseException() {
		super(PurchaseExceptionCode.ACCESS_FOR_NOT_OWNED_PURCHASE);
	}
}
