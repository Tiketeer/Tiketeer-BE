package com.tiketeer.Tiketeer.domain.ticketing.usecase;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.EventTimeNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingStockService;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.CreateTicketingResultDto;

@Service
public class TicketingCreateUseCase {
	private final TicketingService ticketingService;
	private final TicketingStockService ticketingStockService;
	private final MemberRepository memberRepository;

	@Autowired
	public TicketingCreateUseCase(TicketingService ticketingService, TicketingStockService ticketingStockService,
		MemberRepository memberRepository) {
		this.ticketingService = ticketingService;
		this.ticketingStockService = ticketingStockService;
		this.memberRepository = memberRepository;
	}

	@Transactional
	public CreateTicketingResultDto createTicketing(CreateTicketingCommandDto command) {
		var now = command.getCommandCreatedAt();
		var saleStart = command.getSaleStart();
		var saleEnd = command.getSaleEnd();
		var eventTime = command.getEventTime();

		if (!isEventTimeValid(now, eventTime)) {
			throw new EventTimeNotValidException();
		}

		if (!isSaleDurationValid(now, saleStart, saleEnd)) {
			throw new SaleDurationNotValidException();
		}

		var member = memberRepository.findByEmail(command.getMemberEmail())
			.orElseThrow(MemberNotFoundException::new);

		var ticketing = ticketingService.saveTicketing(
			Ticketing.builder()
				.member(member)
				.title(command.getTitle())
				.description(command.getDescription())
				.location(
					command.getLocation())
				.category(command.getCategory())
				.runningMinutes(command.getRunningMinutes())
				.price(command.getPrice())
				.eventTime(command.getEventTime())
				.saleStart(command.getSaleStart())
				.saleEnd(command.getSaleEnd())
				.build());

		ticketingStockService.createStock(ticketing.getId(), command.getStock());

		return CreateTicketingResultDto.builder()
			.ticketingId(ticketing.getId())
			.createdAt(ticketing.getCreatedAt())
			.build();
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
