package com.tiketeer.Tiketeer.domain.ticketing.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.auth.SecurityContextHelper;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.GetAllTicketingsResponseDto;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.GetTicketingResponseDto;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.PatchTicketingRequestDto;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.PostTicketingRequestDto;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.PostTicketingResponseDto;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.CreateTicketingUseCase;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.DeleteTicketingUseCase;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.UpdateTicketingUseCase;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.DeleteTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.GetTicketingCommandDto;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ticketings")
public class TicketingController {
	private final TicketingService ticketingService;
	private final SecurityContextHelper securityContextHelper;
	private final CreateTicketingUseCase createTicketingUseCase;
	private final UpdateTicketingUseCase updateTicketingUseCase;
	private final DeleteTicketingUseCase deleteTicketingUseCase;

	@Autowired
	public TicketingController(TicketingService ticketingService, SecurityContextHelper securityContextHelper,
		CreateTicketingUseCase createTicketingUseCase, UpdateTicketingUseCase updateTicketingUseCase,
		DeleteTicketingUseCase deleteTicketingUseCase) {
		this.ticketingService = ticketingService;
		this.securityContextHelper = securityContextHelper;
		this.createTicketingUseCase = createTicketingUseCase;
		this.updateTicketingUseCase = updateTicketingUseCase;
		this.deleteTicketingUseCase = deleteTicketingUseCase;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<GetAllTicketingsResponseDto>>> getAllTicketings() {
		var results = ticketingService.getAllTicketings();
		var responseBody = ApiResponse.wrap(
			results.stream().map(GetAllTicketingsResponseDto::convertFromDto).toList());
		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
	}

	@GetMapping(path = "/{ticketingId}")
	public ResponseEntity<ApiResponse<GetTicketingResponseDto>> getTicketing(@PathVariable UUID ticketingId) {
		var result = ticketingService.getTickting(
			GetTicketingCommandDto.builder().ticketingId(ticketingId).build());

		var responseBody = ApiResponse.wrap(GetTicketingResponseDto.convertFromDto(result));
		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
	}

	@PostMapping
	public ResponseEntity<ApiResponse<PostTicketingResponseDto>> postTicketing(
		@Valid @RequestBody PostTicketingRequestDto request) {
		var memberEmail = securityContextHelper.getEmailInToken();
		var result = createTicketingUseCase.createTicketing(request.convertToDto(memberEmail));
		var responseBody = ApiResponse.wrap(PostTicketingResponseDto.convertFromDto(result));
		return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
	}

	@PatchMapping(path = "/{ticketingId}")
	public ResponseEntity<?> patchTicketing(@PathVariable String ticketingId,
		@RequestBody PatchTicketingRequestDto request) {
		var memberEmail = securityContextHelper.getEmailInToken();
		updateTicketingUseCase.updateTicketing(request.convertToDto(ticketingId, memberEmail));
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(path = "/{ticketingId}")
	public ResponseEntity<?> deleteTicketing(@PathVariable String ticketingId) {
		var memberEmail = securityContextHelper.getEmailInToken();
		deleteTicketingUseCase.deleteTicketing(DeleteTicketingCommandDto.builder()
			.ticketingId(UUID.fromString(ticketingId))
			.memberEmail(memberEmail)
			.build());
		return ResponseEntity.ok().build();
	}
}
