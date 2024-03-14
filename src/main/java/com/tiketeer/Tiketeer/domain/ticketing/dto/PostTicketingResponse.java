package com.tiketeer.Tiketeer.domain.ticketing.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PostTicketingResponse {
	private final UUID ticketingId;
	private final LocalDateTime createdAt;

	@Builder
	public PostTicketingResponse(UUID ticketingId, LocalDateTime createdAt) {
		this.ticketingId = ticketingId;
		this.createdAt = createdAt;
	}

	public static PostTicketingResponse convertFromDto(CreateTicketingResult dto) {
		return PostTicketingResponse.builder()
			.ticketingId(dto.getTicketingId()).createdAt(dto.getCreatedAt()).build();
	}
}
