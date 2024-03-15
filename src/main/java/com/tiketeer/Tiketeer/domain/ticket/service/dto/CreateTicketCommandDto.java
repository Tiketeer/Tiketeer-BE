package com.tiketeer.Tiketeer.domain.ticket.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateTicketCommandDto {
	private final UUID ticketId;
	private final int numOfTickets;
	private LocalDateTime commandCreatedAt = LocalDateTime.now();

	@Builder
	public CreateTicketCommandDto(UUID ticketId, int numOfTickets, LocalDateTime commandCreatedAt) {
		this.ticketId = ticketId;
		this.numOfTickets = numOfTickets;
		if (commandCreatedAt != null) {
			this.commandCreatedAt = commandCreatedAt;
		}
	}
}
