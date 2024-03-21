package com.tiketeer.Tiketeer.domain.ticket.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@Service
@Transactional(readOnly = true)
public class TicketService {
	private final TicketRepository ticketRepository;
	private final TicketingRepository ticketingRepository;

	@Autowired
	public TicketService(TicketRepository ticketRepository, TicketingRepository ticketingRepository) {
		this.ticketRepository = ticketRepository;
		this.ticketingRepository = ticketingRepository;
	}

	public List<Ticket> listTicketByTicketingId(UUID ticketingId) {
		var ticketing = findTicketingById(ticketingId);
		return ticketRepository.findAllByTicketing(ticketing);
	}

	@Transactional
	public void createTickets(UUID ticketingId, int numOfTickets) {
		var ticketing = findTicketingById(ticketingId);

		ticketRepository.saveAll(Arrays.stream(new int[numOfTickets])
			.mapToObj(i -> Ticket.builder().ticketing(ticketing).build())
			.toList());
	}

	@Transactional
	public void deleteAllByTicketIds(List<UUID> ticketIds) {
		ticketRepository.deleteAllByIdInBatch(ticketIds);
	}

	private Ticketing findTicketingById(UUID ticketingId) {
		return ticketingRepository.findById(ticketingId).orElseThrow(TicketingNotFoundException::new);
	}
}
