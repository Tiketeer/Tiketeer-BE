package com.tiketeer.Tiketeer.domain.ticketing.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.CreateTicketingResultDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PostTicketingResponseDto {
	private final UUID ticketingId;
	private final LocalDateTime createdAt;

	@Builder
	public PostTicketingResponseDto(UUID ticketingId, LocalDateTime createdAt) {
		this.ticketingId = ticketingId;
		this.createdAt = createdAt;
	}

	public static PostTicketingResponseDto convertFromDto(CreateTicketingResultDto dto) {
		return PostTicketingResponseDto.builder()
			.ticketingId(dto.getTicketingId()).createdAt(dto.getCreatedAt()).build();
	}
}
