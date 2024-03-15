package com.tiketeer.Tiketeer.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tiketeer.Tiketeer.domain.member.exception.DuplicatedEmailException;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidOtpException;
import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.ticket.exception.TicketNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.EventTimeNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.exception.code.CommonExceptionCode;
import com.tiketeer.Tiketeer.exception.code.ExceptionCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler({
		// Member
		DuplicatedEmailException.class,
		MemberNotFoundException.class,
		InvalidOtpException.class,

		// Ticketing
		TicketingNotFoundException.class,
		EventTimeNotValidException.class,
		SaleDurationNotValidException.class,

		// Ticket
		TicketNotFoundException.class
	})
	protected ResponseEntity<ErrorResponse> handleDefinedException(final DefinedException e) {
		logError(e);
		return createErrorResponse(e.getExceptionCode());
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleUndefinedException(final Exception e) {
		logError(e);
		return createErrorResponse(CommonExceptionCode.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ErrorResponse> createErrorResponse(ExceptionCode exceptionCode) {
		return ResponseEntity.status(exceptionCode.getHttpStatus()).body(ErrorResponse.builder()
			.code(exceptionCode.name())
			.message(exceptionCode.getMessage())
			.build());
	}

	private void logError(final Exception e) {
		log.error(e.getClass().getName(), e);
	}
}
