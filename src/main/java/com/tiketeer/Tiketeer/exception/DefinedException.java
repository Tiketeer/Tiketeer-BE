package com.tiketeer.Tiketeer.exception;

import com.tiketeer.Tiketeer.exception.code.ExceptionCode;

import lombok.Getter;

@Getter
public class DefinedException extends RuntimeException {
	private final ExceptionCode exceptionCode;

	protected DefinedException(ExceptionCode exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	protected DefinedException(ExceptionCode exceptionCode, String message) {
		super(message);
		this.exceptionCode = exceptionCode;
	}
}
