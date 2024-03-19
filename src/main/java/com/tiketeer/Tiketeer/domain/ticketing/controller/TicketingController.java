package com.tiketeer.Tiketeer.domain.ticketing.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.PatchTicketingRequestDto;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.PostTicketingRequestDto;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.PostTicketingResponseDto;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.DeleteTicketingCommandDto;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ticketings")
public class TicketingController {
	private final TicketingService ticketingService;

	@Autowired
	public TicketingController(TicketingService ticketingService) {
		this.ticketingService = ticketingService;
	}

	@PostMapping(path = "/")
	public ResponseEntity<ApiResponse<PostTicketingResponseDto>> postTicketing(
		@Valid @RequestBody PostTicketingRequestDto request) {
		// TODO: JWT 구현이 완료되면 SecurityContext를 통해 가져오는 것으로 대체
		var memberEmail = "mock@mock.com";
		var result = ticketingService.createTicketing(request.convertToDto(memberEmail));
		var responseBody = ApiResponse.wrap(PostTicketingResponseDto.convertFromDto(result));
		return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
	}

	@PatchMapping(path = "/{ticketingId}")
	public ResponseEntity patchTicketing(@PathVariable String ticketingId,
		@RequestBody PatchTicketingRequestDto request) {
		// TODO: JWT 구현이 완료되면 SecurityContext를 통해 가져오는 것으로 대체
		var memberEmail = "mock@mock.com";
		ticketingService.updateTicketing(request.convertToDto(ticketingId, memberEmail));
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(path = "/{ticketingId}")
	public ResponseEntity deleteTicketing(@PathVariable String ticketingId) {
		// TODO: JWT 구현이 완료되면 SecurityContext를 통해 가져오는 것으로 대체
		var memberEmail = "mock@mock.com";
		ticketingService.deleteTicketing(DeleteTicketingCommandDto.builder()
			.ticketingId(UUID.fromString(ticketingId))
			.memberEmail(memberEmail)
			.build());
		return ResponseEntity.ok().build();
	}
}
