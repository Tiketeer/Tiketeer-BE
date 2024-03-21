package com.tiketeer.Tiketeer.domain.ticketing.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticket.usecase.TicketService;
import com.tiketeer.Tiketeer.domain.ticket.usecase.dto.DropAllTicketsUnderSomeTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.DeleteTicketingAfterSaleStartException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.ModifyForNotOwnedTicketingException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.DeleteTicketingCommandDto;

@Service
@Transactional(readOnly = true)
public class DeleteTicketingUseCase {

	private final TicketingRepository ticketingRepository;
	private final TicketService ticketService;

	@Autowired
	public DeleteTicketingUseCase(TicketingRepository ticketingRepository, TicketService ticketService
	) {
		this.ticketingRepository = ticketingRepository;
		this.ticketService = ticketService;
	}

	@Transactional
	public void deleteTicketing(DeleteTicketingCommandDto command) {
		var ticketingId = command.getTicketingId();
		var ticketing = ticketingRepository.findById(ticketingId).orElseThrow(TicketingNotFoundException::new);

		validateTicketingOwnership(ticketing, command.getMemberEmail());

		var now = command.getCommandCreatedAt();
		if (now.isAfter(ticketing.getSaleStart())) {
			throw new DeleteTicketingAfterSaleStartException();
		}

		var dropTicketsCommand = DropAllTicketsUnderSomeTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.commandCreatedAt(now)
			.build();

		ticketService.dropAllTicketsUnderSomeTicketing(dropTicketsCommand);
		ticketingRepository.delete(ticketing);
	}

	private void validateTicketingOwnership(Ticketing ticketing, String email) {
		if (!ticketing.getMember().getEmail().equals(email)) {
			throw new ModifyForNotOwnedTicketingException();
		}
		return;
	}
}
