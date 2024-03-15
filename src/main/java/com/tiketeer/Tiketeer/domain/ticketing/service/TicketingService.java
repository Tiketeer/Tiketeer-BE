package com.tiketeer.Tiketeer.domain.ticketing.service;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.EventTimeNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.CreateTicketingResultDto;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.UpdateTicketingCommandDto;

@Service
@Transactional(readOnly = true)
public class TicketingService {
	private final TicketingRepository ticketingRepository;
	private final TicketRepository ticketRepository;
	private final MemberRepository memberRepository;

	@Autowired
	public TicketingService(TicketingRepository ticketingRepository, TicketRepository ticketRepository,
		MemberRepository memberRepository) {
		this.ticketingRepository = ticketingRepository;
		this.ticketRepository = ticketRepository;
		this.memberRepository = memberRepository;
	}

	@Transactional
	public CreateTicketingResultDto createTicketing(CreateTicketingCommandDto command) {
		var eventTime = command.getEventTime();
		var saleStart = command.getSaleStart();
		var saleEnd = command.getSaleEnd();

		var now = LocalDateTime.now();

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

		createNumOfTickets(ticketing, command.getStock());

		return CreateTicketingResultDto.builder()
			.ticketingId(ticketing.getId())
			.createdAt(ticketing.getCreatedAt())
			.build();
	}

	private void createNumOfTickets(Ticketing ticketing, int num) {
		ticketRepository.saveAll(Arrays.stream(new int[num])
			.mapToObj(i -> Ticket.builder().ticketing(ticketing).build())
			.toList());
	}

	@Transactional
	public void updateTicketing(UpdateTicketingCommandDto command) {
		var eventTime = command.getEventTime();
		var saleStart = command.getSaleStart();
		var saleEnd = command.getSaleEnd();

		var now = LocalDateTime.now();

		validateTicketingMetadataBeforeSave(now, eventTime, saleStart, saleEnd);

		var ticketingId = command.getTicketingId();
		var ticketing = ticketingRepository.findById(ticketingId)
			.orElseThrow(TicketingNotFoundException::new);

		if (now.isAfter(ticketing.getSaleStart())) {
			throw new RuntimeException();
		}

		ticketing.setTitle(command.getTitle());
		if (command.getDescription() != null) {
			ticketing.setDescription(command.getDescription());
		}
		ticketing.setPrice(command.getPrice());
		ticketing.setLocation(command.getLocation());
		ticketing.setEventTime(eventTime);
		ticketing.setSaleStart(saleStart);
		ticketing.setSaleEnd(saleEnd);
		ticketing.setCategory(command.getCategory());
		ticketing.setRunningMinutes(command.getRunningMinutes());
		updateStock(ticketing, command.getStock());
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

	private void updateStock(Ticketing ticketing, int newStock) {
		var tickets = ticketRepository.findAllByTicketing(ticketing);
		var numOfTickets = tickets.size();
		if (numOfTickets > newStock) {
			var ticketIdsForDelete = tickets.stream()
				.limit(numOfTickets - newStock)
				.map(Ticket::getId).toList();
			ticketRepository.deleteAllById(ticketIdsForDelete);
		} else if (numOfTickets < newStock) {
			createNumOfTickets(ticketing, newStock - numOfTickets);
		}
		return;
	}
}
