package com.tiketeer.Tiketeer.domain.ticket.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DeleteTicketCommandDto {
	private final UUID ticketingId;
	private final int numOfTickets;
	private LocalDateTime commandCreatedAt = LocalDateTime.now();

	@Builder
	public DeleteTicketCommandDto(UUID ticketingId, int numOfTickets, LocalDateTime commandCreatedAt) {
		this.ticketingId = ticketingId;
		this.numOfTickets = numOfTickets;
		if (commandCreatedAt != null) {
			this.commandCreatedAt = commandCreatedAt;
		}
	}
}
