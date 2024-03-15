package com.tiketeer.Tiketeer.domain.ticket.service.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DeleteTicketCommandDto {
	private final List<UUID> ticketIds;
	private LocalDateTime commandCreatedAt = LocalDateTime.now();

	@Builder
	public DeleteTicketCommandDto(List<UUID> ticketIds, LocalDateTime commandCreatedAt) {
		this.ticketIds = ticketIds;
		if (commandCreatedAt != null) {
			this.commandCreatedAt = commandCreatedAt;
		}
	}
}
