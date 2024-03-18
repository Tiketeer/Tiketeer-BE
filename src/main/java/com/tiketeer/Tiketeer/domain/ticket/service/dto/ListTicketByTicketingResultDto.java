package com.tiketeer.Tiketeer.domain.ticket.service.dto;

import java.util.List;

import com.tiketeer.Tiketeer.domain.ticket.Ticket;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ListTicketByTicketingResultDto {
	private final List<Ticket> tickets;

	@Builder
	public ListTicketByTicketingResultDto(List<Ticket> tickets) {
		this.tickets = tickets;
	}
}
