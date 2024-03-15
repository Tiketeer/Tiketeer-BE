package com.tiketeer.Tiketeer.domain.ticket.service.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateTicketCommandDto {
	private final UUID ticketId;
	private final int numOfTickets;

	@Builder
	public CreateTicketCommandDto(UUID ticketId, int numOfTickets) {
		this.ticketId = ticketId;
		this.numOfTickets = numOfTickets;
	}
}
