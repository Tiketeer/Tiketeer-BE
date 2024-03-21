package com.tiketeer.Tiketeer.domain.ticket.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DropAllTicketsUnderSomeTicketingCommandDto {
	private final UUID ticketingId;
	private LocalDateTime commandCreatedAt = LocalDateTime.now();

	@Builder
	public DropAllTicketsUnderSomeTicketingCommandDto(UUID ticketingId, LocalDateTime commandCreatedAt) {
		this.ticketingId = ticketingId;
		if (commandCreatedAt != null) {
			this.commandCreatedAt = commandCreatedAt;
		}
	}
}
