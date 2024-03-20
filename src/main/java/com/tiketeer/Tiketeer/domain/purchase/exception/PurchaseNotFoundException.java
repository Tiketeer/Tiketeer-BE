package com.tiketeer.Tiketeer.domain.purchase.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.PurchaseExceptionCode;

public class PurchaseNotFoundException extends DefinedException {
	public PurchaseNotFoundException() {
		super(PurchaseExceptionCode.PURCHASE_NOT_FOUND);
	}
}
