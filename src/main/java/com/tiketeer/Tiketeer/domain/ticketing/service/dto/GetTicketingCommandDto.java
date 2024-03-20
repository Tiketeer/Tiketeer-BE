package com.tiketeer.Tiketeer.domain.ticketing.service.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GetTicketingCommandDto {
	private UUID ticketingId;

	@Builder
	public GetTicketingCommandDto(UUID ticketingId) {
		this.ticketingId = ticketingId;
	}
}
