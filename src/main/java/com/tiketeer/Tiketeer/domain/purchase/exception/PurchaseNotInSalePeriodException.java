package com.tiketeer.Tiketeer.domain.purchase.exception;

import com.tiketeer.Tiketeer.exception.DefinedException;
import com.tiketeer.Tiketeer.exception.code.PurchaseExceptionCode;

public class PurchaseNotInSalePeriodException extends DefinedException {
	public PurchaseNotInSalePeriodException() {
		super(PurchaseExceptionCode.PURCHASE_NOT_IN_SALE_PERIOD);
	}
}
