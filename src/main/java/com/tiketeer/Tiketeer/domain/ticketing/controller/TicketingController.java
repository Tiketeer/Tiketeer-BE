package com.tiketeer.Tiketeer.domain.ticketing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.ticketing.dto.PostTicketingRequest;
import com.tiketeer.Tiketeer.domain.ticketing.dto.PostTicketingResponse;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
public class TicketingController {
	private final TicketingService ticketingService;

	@Autowired
	public TicketingController(TicketingService ticketingService) {
		this.ticketingService = ticketingService;
	}

	@PostMapping(path = "/ticketings")
	public ResponseEntity<ApiResponse<PostTicketingResponse>> postTicketing(
		@Valid @RequestBody PostTicketingRequest postCommand) {
		// TODO: JWT 구현이 완료되면 SecurityContext를 통해 가져오는 것으로 대체
		var memberEmail = "mock@mock.com";
		var result = ticketingService.createTicketing(postCommand.convertToDto(memberEmail));
		var responseBody = ApiResponse.wrap(PostTicketingResponse.convertFromDto(result));
		return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
	}
}
