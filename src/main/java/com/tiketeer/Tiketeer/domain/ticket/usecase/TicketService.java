package com.tiketeer.Tiketeer.domain.ticket.usecase;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticket.usecase.dto.CreateTicketCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.usecase.dto.DropAllTicketsUnderSomeTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.usecase.dto.DropNumOfTicketsUnderSomeTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.usecase.dto.ListTicketByTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.usecase.dto.ListTicketByTicketingResultDto;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
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
		var ticketing = findTicketingById(command.getTicketingId());
		return ListTicketByTicketingResultDto.builder()
			.tickets(ticketRepository.findAllByTicketing(ticketing))
			.build();
	}

	@Transactional
	public void createTickets(CreateTicketCommandDto command) {
		var ticketing = findTicketingById(command.getTicketingId());

		if (command.getCommandCreatedAt().isAfter(ticketing.getSaleStart())) {
			throw new UpdateTicketingAfterSaleStartException();
		}

		ticketRepository.saveAll(Arrays.stream(new int[command.getNumOfTickets()])
			.mapToObj(i -> Ticket.builder().ticketing(ticketing).build())
			.toList());
	}

	@Transactional
	public void dropNumOfTicketsUnderSomeTicketing(DropNumOfTicketsUnderSomeTicketingCommandDto command) {
		var ticketing = findTicketingById(command.getTicketingId());

		if (command.getCommandCreatedAt().isAfter(ticketing.getSaleStart())) {
			throw new UpdateTicketingAfterSaleStartException();
		}

		var tickets = listTicketByTicketing(
			ListTicketByTicketingCommandDto.builder().ticketingId(ticketing.getId()).build()).getTickets();

		var ticketIdsForDelete = tickets.stream()
			.limit(command.getNumOfTickets())
			.map(Ticket::getId).toList();

		ticketRepository.deleteAllByIdInBatch(ticketIdsForDelete);
	}

	@Transactional
	public void dropAllTicketsUnderSomeTicketing(DropAllTicketsUnderSomeTicketingCommandDto command) {
		var ticketing = findTicketingById(command.getTicketingId());

		if (command.getCommandCreatedAt().isAfter(ticketing.getSaleStart())) {
			throw new UpdateTicketingAfterSaleStartException();
		}

		var tickets = listTicketByTicketing(
			ListTicketByTicketingCommandDto.builder().ticketingId(ticketing.getId()).build()).getTickets();

		var ticketIdsForDelete = tickets.stream()
			.map(Ticket::getId).toList();
		ticketRepository.deleteAllByIdInBatch(ticketIdsForDelete);
	}

	private Ticketing findTicketingById(UUID ticketingId) {
		return ticketingRepository.findById(ticketingId).orElseThrow(TicketingNotFoundException::new);
	}
}
