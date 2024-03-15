package com.tiketeer.Tiketeer.domain.ticket.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticket.service.dto.CreateTicketCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.service.dto.ListTicketByTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.service.dto.ListTicketByTicketingResultDto;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.UpdateTicketingAfterSaleStartException;
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

	public ListTicketByTicketingResultDto listTicketByTicketing(ListTicketByTicketingCommandDto command) {
		var ticketing = ticketingRepository.findById(command.getTicketingId())
			.orElseThrow(TicketingNotFoundException::new);
		return ListTicketByTicketingResultDto.builder()
			.tickets(ticketRepository.findAllByTicketing(ticketing))
			.build();
	}

	@Transactional
	public void createTickets(CreateTicketCommandDto command) {
		var ticketing = ticketingRepository.findById(command.getTicketId())
			.orElseThrow(TicketingNotFoundException::new);

		if (command.getCommandCreatedAt().isAfter(ticketing.getSaleStart())) {
			throw new UpdateTicketingAfterSaleStartException();
		}

		ticketRepository.saveAll(Arrays.stream(new int[command.getNumOfTickets()])
			.mapToObj(i -> Ticket.builder().ticketing(ticketing).build())
			.toList());
	}

	@Transactional
	public void deleteTickets(List<UUID> ticketsIds) {
		ticketRepository.deleteAllById(ticketsIds);
	}
}
