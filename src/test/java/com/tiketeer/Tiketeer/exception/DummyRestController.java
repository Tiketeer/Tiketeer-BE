package com.tiketeer.Tiketeer.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.member.exception.DuplicatedEmailException;
import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.ticket.exception.TicketNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.exception.code.CommonExceptionCode;
import com.tiketeer.Tiketeer.exception.code.MemberExceptionCode;
import com.tiketeer.Tiketeer.exception.code.TicketExceptionCode;
import com.tiketeer.Tiketeer.exception.code.TicketingExceptionCode;

@RestController
public class DummyRestController {
	@GetMapping("/test/exception-handler/{errorCode}")
	public ResponseEntity throwException(String errorCode) {
		if (errorCode.equals(CommonExceptionCode.INTERNAL_SERVER_ERROR.name())) {
			throw new RuntimeException();
		} else if (errorCode.equals(MemberExceptionCode.DUPLICATED_EMAIL.name())) {
			throw new DuplicatedEmailException();
		} else if (errorCode.equals(MemberExceptionCode.MEMBER_NOT_FOUND.name())) {
			throw new MemberNotFoundException();
		} else if (errorCode.equals(TicketingExceptionCode.TICKETING_NOT_FOUND.name())) {
			throw new TicketingNotFoundException();
		} else if (errorCode.equals(TicketExceptionCode.TICKET_NOT_FOUND.name())) {
			throw new TicketNotFoundException();
		} else {
			return ResponseEntity.ok().build();
		}
	}
}
