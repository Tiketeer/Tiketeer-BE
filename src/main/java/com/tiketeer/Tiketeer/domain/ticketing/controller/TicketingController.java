package com.tiketeer.Tiketeer.domain.ticketing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.PostTicketingRequestDto;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.PostTicketingResponseDto;
import com.tiketeer.Tiketeer.domain.ticketing.dto.PatchTicketingRequest;
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
	public ResponseEntity<ApiResponse<PostTicketingResponseDto>> postTicketing(
		@Valid @RequestBody PostTicketingRequestDto request) {
		// TODO: JWT 구현이 완료되면 SecurityContext를 통해 가져오는 것으로 대체
		var memberEmail = "mock@mock.com";
		var result = ticketingService.createTicketing(request.convertToDto(memberEmail));
		var responseBody = ApiResponse.wrap(PostTicketingResponseDto.convertFromDto(result));
		return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
	}

	@PatchMapping(path = "/ticketings/{ticketing_id}")
	public ResponseEntity patchTicketing(@RequestBody PatchTicketingRequest request) {
		// TODO: JWT 구현이 완료되면 SecurityContext를 통해 가져오는 것으로 대체
		var memberEmail = "mock@mock.com";
		return ResponseEntity.ok().build();
	}
}
