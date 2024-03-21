package com.tiketeer.Tiketeer.domain.ticketing.usecase;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticket.usecase.TicketService;
import com.tiketeer.Tiketeer.domain.ticket.usecase.dto.CreateTicketCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.usecase.dto.DropNumOfTicketsUnderSomeTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.usecase.dto.ListTicketByTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.EventTimeNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.ModifyForNotOwnedTicketingException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.UpdateTicketingAfterSaleStartException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.UpdateTicketingCommandDto;

@Service
@Transactional(readOnly = true)
public class UpdateTicketingUseCase {

	private final TicketingRepository ticketingRepository;
	private final TicketService ticketService;
	private final MemberRepository memberRepository;
	private final TicketRepository ticketRepository;

	@Autowired
	public UpdateTicketingUseCase(TicketingRepository ticketingRepository, TicketService ticketService,
		MemberRepository memberRepository, TicketRepository ticketRepository) {
		this.ticketingRepository = ticketingRepository;
		this.ticketService = ticketService;
		this.memberRepository = memberRepository;
		this.ticketRepository = ticketRepository;
	}

	@Transactional
	public void updateTicketing(UpdateTicketingCommandDto command) {
		var ticketingId = command.getTicketingId();
		var ticketing = ticketingRepository.findById(ticketingId)
			.orElseThrow(TicketingNotFoundException::new);

		validateTicketingOwnership(ticketing, command.getEmail());

		var now = command.getCommandCreatedAt();
		if (now.isAfter(ticketing.getSaleStart())) {
			throw new UpdateTicketingAfterSaleStartException();
		}

		var eventTime = command.getEventTime();
		var saleStart = command.getSaleStart();
		var saleEnd = command.getSaleEnd();

		validateTicketingMetadataBeforeSave(now, eventTime, saleStart, saleEnd);

		ticketing.setTitle(command.getTitle());
		ticketing.setDescription(command.getDescription());
		ticketing.setPrice(command.getPrice());
		ticketing.setLocation(command.getLocation());
		ticketing.setEventTime(eventTime);
		ticketing.setSaleStart(saleStart);
		ticketing.setSaleEnd(saleEnd);
		ticketing.setCategory(command.getCategory());
		ticketing.setRunningMinutes(command.getRunningMinutes());
		updateStock(ticketing, command.getStock(), now);
	}

	private void validateTicketingOwnership(Ticketing ticketing, String email) {
		if (!ticketing.getMember().getEmail().equals(email)) {
			throw new ModifyForNotOwnedTicketingException();
		}
		return;
	}

	private void validateTicketingMetadataBeforeSave(LocalDateTime now, LocalDateTime eventTime,
		LocalDateTime saleStart, LocalDateTime saleEnd) {
		if (eventTime == null || !isEventTimeValid(now, eventTime)) {
			throw new EventTimeNotValidException();
		}
		if (saleStart == null
			|| saleEnd == null
			|| !isSaleDurationValid(now, saleStart, saleEnd)
			|| !isEventTimeAndSaleEndValid(eventTime, saleEnd)) {
			throw new SaleDurationNotValidException();
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

	private boolean isEventTimeAndSaleEndValid(LocalDateTime eventTime, LocalDateTime saleEnd) {
		return eventTime.isAfter(saleEnd);
	}

	private void updateStock(Ticketing ticketing, int newStock, LocalDateTime now) {
		var tickets = ticketService.listTicketByTicketing(
				ListTicketByTicketingCommandDto.builder().ticketingId(ticketing.getId()).build())
			.getTickets();

		var numOfTickets = tickets.size();
		if (numOfTickets > newStock) {
			ticketService.dropNumOfTicketsUnderSomeTicketing(DropNumOfTicketsUnderSomeTicketingCommandDto.builder()
				.ticketingId(ticketing.getId())
				.numOfTickets(numOfTickets - newStock)
				.commandCreatedAt(now).build());

		} else if (numOfTickets < newStock) {
			ticketService.createTickets(CreateTicketCommandDto.builder()
				.ticketingId(ticketing.getId())
				.numOfTickets(newStock - numOfTickets)
				.commandCreatedAt(now).build());
		}
		return;
	}

}
