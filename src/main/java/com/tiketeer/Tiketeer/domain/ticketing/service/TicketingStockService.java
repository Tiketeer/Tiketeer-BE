package com.tiketeer.Tiketeer.domain.ticketing.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.service.TicketService;

@Service
@Transactional(readOnly = true)
public class TicketingStockService {
	private final TicketService ticketService;
	private final TicketingService ticketingService;

	@Autowired
	public TicketingStockService(TicketService ticketService, TicketingService ticketingService) {
		this.ticketService = ticketService;
		this.ticketingService = ticketingService;
	}

	@Transactional
	public void createStock(UUID ticketingId, int stock) {
		var ticketing = ticketingService.findById(ticketingId);
		ticketService.createTickets(ticketing.getId(), stock);
	}

	@Transactional
	public void updateStock(UUID ticketingId, int newStock) {
		var tickets = ticketService.listTicketByTicketingId(ticketingId);

		var numOfTickets = tickets.size();
		if (numOfTickets > newStock) {
			dropNumOfTicketsByTicketing(ticketingId, numOfTickets - newStock);

		} else if (numOfTickets < newStock) {
			createStock(ticketingId, newStock - numOfTickets);
		}
	}

	@Transactional
	public void dropAllStock(UUID ticketingId) {
		var tickets = ticketService.listTicketByTicketingId(ticketingId);

		var ticketIdsForDelete = tickets.stream()
			.map(Ticket::getId).toList();
		ticketService.deleteAllByTicketIds(ticketIdsForDelete);
	}

	private void dropNumOfTicketsByTicketing(UUID ticketingId, int numOfTickets) {
		var ticketing = ticketingService.findById(ticketingId);

		var tickets = ticketService.listTicketByTicketingId(ticketing.getId());

		var ticketIdsForDelete = tickets.stream()
			.limit(numOfTickets)
			.map(Ticket::getId).toList();

		ticketService.deleteAllByTicketIds(ticketIdsForDelete);
	}
}
