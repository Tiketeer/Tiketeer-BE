package com.tiketeer.Tiketeer.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tiketeer.Tiketeer.exception.code.CommonExceptionCode;
import com.tiketeer.Tiketeer.exception.code.ExceptionCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(DefinedException.class)
	protected ResponseEntity<ErrorResponse> handleDefinedException(final DefinedException ex) {
		logError(ex);
		return createErrorResponse(ex.getExceptionCode());
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleUndefinedException(final Exception ex) {
		logError(ex);
		return createErrorResponse(CommonExceptionCode.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ErrorResponse> createErrorResponse(ExceptionCode exceptionCode) {
		return ResponseEntity.status(exceptionCode.getHttpStatus()).body(ErrorResponse.builder()
			.code(exceptionCode.name())
			.message(exceptionCode.getMessage())
			.build());
	}

	private void logError(final Exception ex) {
		log.error(ex.getClass().getName(), ex);
	}
}
