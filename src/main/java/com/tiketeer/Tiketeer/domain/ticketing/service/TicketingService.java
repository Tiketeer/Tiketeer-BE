package com.tiketeer.Tiketeer.domain.ticketing.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.ticket.service.TicketService;
import com.tiketeer.Tiketeer.domain.ticket.service.dto.CreateTicketCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.service.dto.DropAllTicketsUnderSomeTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.service.dto.DropNumOfTicketsUnderSomeTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.service.dto.ListTicketByTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.DeleteTicketingAfterSaleStartException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.EventTimeNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.UpdateTicketingAfterSaleStartException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.CreateTicketingResultDto;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.DeleteTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.UpdateTicketingCommandDto;

@Service
@Transactional(readOnly = true)
public class TicketingService {
	private final TicketingRepository ticketingRepository;
	private final TicketService ticketService;
	private final MemberRepository memberRepository;

	@Autowired
	public TicketingService(TicketingRepository ticketingRepository, TicketService ticketService,
		MemberRepository memberRepository) {
		this.ticketingRepository = ticketingRepository;
		this.ticketService = ticketService;
		this.memberRepository = memberRepository;
	}

	@Transactional
	public CreateTicketingResultDto createTicketing(CreateTicketingCommandDto command) {
		var eventTime = command.getEventTime();
		var saleStart = command.getSaleStart();
		var saleEnd = command.getSaleEnd();

		var now = command.getCommandCreatedAt();

		validateTicketingMetadataBeforeSave(now, eventTime, saleStart, saleEnd);

		var member = memberRepository.findByEmail(command.getMemberEmail())
			.orElseThrow(MemberNotFoundException::new);

		var ticketing = ticketingRepository.save(
			Ticketing.builder()
				.member(member)
				.title(command.getTitle())
				.description(command.getDescription())
				.location(
					command.getLocation())
				.category(command.getCategory())
				.runningMinutes(command.getRunningMinutes())
				.price(command.getPrice())
				.eventTime(eventTime)
				.saleStart(saleStart)
				.saleEnd(saleEnd)
				.build());

		ticketService.createTickets(
			CreateTicketCommandDto.builder()
				.ticketingId(ticketing.getId())
				.numOfTickets(command.getStock())
				.commandCreatedAt(now)
				.build());

		return CreateTicketingResultDto.builder()
			.ticketingId(ticketing.getId())
			.createdAt(ticketing.getCreatedAt())
			.build();
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

	private void validateTicketingOwnership(Ticketing ticketing, String email) {
		if (!ticketing.getMember().getEmail().equals(email)) {
			throw new RuntimeException();
		}
		return;
	}
}
