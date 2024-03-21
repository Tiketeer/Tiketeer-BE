package com.tiketeer.Tiketeer.domain.ticketing.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CreateTicketingResultDto {
	private UUID ticketingId;
	private LocalDateTime createdAt;

	@Builder
	public CreateTicketingResultDto(UUID ticketingId, LocalDateTime createdAt) {
		this.ticketingId = ticketingId;
		this.createdAt = createdAt;
	}
}
