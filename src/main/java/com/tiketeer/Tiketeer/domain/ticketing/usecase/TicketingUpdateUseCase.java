package com.tiketeer.Tiketeer.domain.ticketing.usecase;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.EventTimeNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.ModifyForNotOwnedTicketingException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.UpdateTicketingAfterSaleStartException;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingStockService;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.UpdateTicketingCommandDto;

@Service
public class TicketingUpdateUseCase {
	private final TicketingService ticketingService;
	private final TicketingStockService ticketingStockService;

	@Autowired
	public TicketingUpdateUseCase(TicketingService ticketingService, TicketingStockService ticketingStockService) {
		this.ticketingService = ticketingService;
		this.ticketingStockService = ticketingStockService;
	}

	@Transactional
	public void updateTicketing(UpdateTicketingCommandDto command) {
		var ticketingId = command.getTicketingId();
		var ticketing = ticketingService.findById(ticketingId);

		validateTicketingOwnership(ticketing, command.getEmail());

		var now = command.getCommandCreatedAt();
		if (now.isAfter(ticketing.getSaleStart())) {
			throw new UpdateTicketingAfterSaleStartException();
		}

		var eventTime = command.getEventTime();
		var saleStart = command.getSaleStart();
		var saleEnd = command.getSaleEnd();

		if (!isEventTimeValid(now, eventTime)) {
			throw new EventTimeNotValidException();
		}

		if (!isSaleDurationValid(now, saleStart, saleEnd)) {
			throw new SaleDurationNotValidException();
		}

		ticketingService.validateTicketingMetadata(eventTime, saleStart, saleEnd);

		ticketing.setTitle(command.getTitle());
		ticketing.setDescription(command.getDescription());
		ticketing.setPrice(command.getPrice());
		ticketing.setLocation(command.getLocation());
		ticketing.setEventTime(eventTime);
		ticketing.setSaleStart(saleStart);
		ticketing.setSaleEnd(saleEnd);
		ticketing.setCategory(command.getCategory());
		ticketing.setRunningMinutes(command.getRunningMinutes());
		ticketingStockService.updateStock(ticketing.getId(), command.getStock());
	}

	private void validateTicketingOwnership(Ticketing ticketing, String email) {
		if (!ticketing.getMember().getEmail().equals(email)) {
			throw new ModifyForNotOwnedTicketingException();
		}
		return;
	}

	private boolean isEventTimeValid(LocalDateTime baseTime, LocalDateTime eventTime) {
		return eventTime.isAfter(baseTime);
	}

	private boolean isSaleDurationValid(LocalDateTime baseTime,
		LocalDateTime saleStart,
		LocalDateTime saleEnd) {
		return saleStart.isAfter(baseTime)
			&& saleEnd.isAfter(baseTime)
			&& saleEnd.isAfter(saleStart);
	}
}
