package com.tiketeer.Tiketeer.domain.ticketing.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DeleteTicketingCommandDto {
	private final UUID ticketingId;
	private final String memberEmail;
	private LocalDateTime commandCreatedAt = LocalDateTime.now();

	@Builder
	public DeleteTicketingCommandDto(UUID ticketingId, String memberEmail, LocalDateTime commandCreatedAt) {
		this.ticketingId = ticketingId;
		this.memberEmail = memberEmail;
		if (commandCreatedAt != null) {
			this.commandCreatedAt = commandCreatedAt;
		}
	}
}
