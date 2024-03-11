package com.tiketeer.Tiketeer.exception.code;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {
	String name();

	HttpStatus getHttpStatus();

	String getMessage();
}
