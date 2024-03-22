package com.tiketeer.Tiketeer.domain.ticketing.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticketing.exception.DeleteTicketingAfterSaleStartException;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingStockService;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.DeleteTicketingCommandDto;

@Service
public class DeleteTicketingUseCase {
	private final TicketingService ticketingService;
	private final TicketingStockService ticketingStockService;

	@Autowired
	public DeleteTicketingUseCase(TicketingService ticketingService, TicketingStockService ticketingStockService) {
		this.ticketingService = ticketingService;
		this.ticketingStockService = ticketingStockService;
	}

	@Transactional
	public void deleteTicketing(DeleteTicketingCommandDto command) {
		var ticketingId = command.getTicketingId();
		var ticketing = ticketingService.findById(ticketingId);

		ticketingService.validateTicketingOwnership(ticketing, command.getMemberEmail());

		var now = command.getCommandCreatedAt();
		if (now.isAfter(ticketing.getSaleStart())) {
			throw new DeleteTicketingAfterSaleStartException();
		}

		ticketingStockService.dropAllStock(ticketingId);
		ticketingService.deleteTicketing(ticketingId);
	}
}
