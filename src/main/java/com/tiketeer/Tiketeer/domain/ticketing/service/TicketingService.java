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
import com.tiketeer.Tiketeer.domain.ticketing.dto.CreateTicketingCommand;
import com.tiketeer.Tiketeer.domain.ticketing.dto.CreateTicketingResult;
import com.tiketeer.Tiketeer.domain.ticketing.exception.EventTimeNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

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
	public CreateTicketingResult createTicketing(CreateTicketingCommand command) {
		var eventTime = command.getEventTime();
		var saleStart = command.getSaleStart();
		var saleEnd = command.getSaleEnd();

		validateTicketingMetadata(eventTime, saleStart, saleEnd);

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

		ticketRepository.saveAll(Arrays.stream(new int[command.getStock()])
			.mapToObj(i -> Ticket.builder().ticketing(ticketing).build())
			.toList());

		return CreateTicketingResult.builder()
			.ticketingId(ticketing.getId())
			.createdAt(ticketing.getCreatedAt())
			.build();
	}

	private void validateTicketingMetadata(LocalDateTime eventTime, LocalDateTime saleStart, LocalDateTime saleEnd) {
		var now = LocalDateTime.now();

		if (eventTime.isBefore(now)) {
			throw new EventTimeNotValidException();
		}

		if (saleStart.isBefore(now)
			|| saleEnd.isBefore(now)
			|| saleEnd.isBefore(saleStart)
			|| saleEnd.isAfter(eventTime)) {
			throw new SaleDurationNotValidException();
		}

		return;
	}
}
