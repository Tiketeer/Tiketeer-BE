package com.tiketeer.Tiketeer.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	@GetMapping(path = "/test/exception-handler/{code}")
	public ResponseEntity throwException(@PathVariable String code) {
		if (code.equals(CommonExceptionCode.INTERNAL_SERVER_ERROR.name())) {
			throw new RuntimeException();
		} else if (code.equals(MemberExceptionCode.DUPLICATED_EMAIL.name())) {
			throw new DuplicatedEmailException();
		} else if (code.equals(MemberExceptionCode.MEMBER_NOT_FOUND.name())) {
			throw new MemberNotFoundException();
		} else if (code.equals(TicketingExceptionCode.TICKETING_NOT_FOUND.name())) {
			throw new TicketingNotFoundException();
		} else if (code.equals(TicketExceptionCode.TICKET_NOT_FOUND.name())) {
			throw new TicketNotFoundException();
		} else {
			return ResponseEntity.ok().build();
		}
	}
}
