package com.tiketeer.Tiketeer.domain.ticketing.usecase;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.ticket.usecase.TicketService;
import com.tiketeer.Tiketeer.domain.ticket.usecase.dto.CreateTicketCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.EventTimeNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.CreateTicketingResultDto;

@Service
@Transactional(readOnly = true)
public class CreateTicketingUseCase {
	private final TicketingRepository ticketingRepository;
	private final TicketService ticketService;
	private final MemberRepository memberRepository;

	@Autowired
	public CreateTicketingUseCase(TicketingRepository ticketingRepository, TicketService ticketService,
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

}
