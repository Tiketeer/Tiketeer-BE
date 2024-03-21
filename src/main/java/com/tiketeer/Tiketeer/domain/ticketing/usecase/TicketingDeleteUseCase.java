package com.tiketeer.Tiketeer.domain.ticketing.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.DeleteTicketingAfterSaleStartException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.ModifyForNotOwnedTicketingException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.UpdateTicketingAfterSaleStartException;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingStockService;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.DeleteTicketingCommandDto;

@Service
public class TicketingDeleteUseCase {
	private final TicketingService ticketingService;
	private final TicketingStockService ticketingStockService;

	@Autowired
	public TicketingDeleteUseCase(TicketingService ticketingService, TicketingStockService ticketingStockService) {
		this.ticketingService = ticketingService;
		this.ticketingStockService = ticketingStockService;
	}

	@Transactional
	public void deleteTicketing(DeleteTicketingCommandDto command) {
		var ticketingId = command.getTicketingId();
		var ticketing = ticketingService.findById(ticketingId);

		validateTicketingOwnership(ticketing, command.getMemberEmail());

		var now = command.getCommandCreatedAt();
		if (now.isAfter(ticketing.getSaleStart())) {
			throw new DeleteTicketingAfterSaleStartException();
		}

		if (command.getCommandCreatedAt().isAfter(ticketing.getSaleStart())) {
			throw new UpdateTicketingAfterSaleStartException();
		}

		ticketingStockService.dropAllStock(ticketingId);
		ticketingService.deleteTicketing(ticketingId);
	}

	private void validateTicketingOwnership(Ticketing ticketing, String email) {
		if (!ticketing.getMember().getEmail().equals(email)) {
			throw new ModifyForNotOwnedTicketingException();
		}
		return;
	}
}
