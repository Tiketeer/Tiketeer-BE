package com.tiketeer.Tiketeer.domain.ticket.service.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ListTicketByTicketingCommandDto {
	private final UUID ticketingId;

	@Builder
	public ListTicketByTicketingCommandDto(UUID ticketingId) {
		this.ticketingId = ticketingId;
	}
}
