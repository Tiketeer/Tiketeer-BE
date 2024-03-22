package com.tiketeer.Tiketeer.domain.ticketing.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticket.service.TicketService;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.EventTimeNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.ModifyForNotOwnedTicketingException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.GetAllTicketingsResultDto;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.GetTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.GetTicketingResultDto;

@Service
@Transactional(readOnly = true)
public class TicketingService {
	private final TicketingRepository ticketingRepository;
	private final TicketService ticketService;
	private final TicketRepository ticketRepository;

	@Autowired
	public TicketingService(TicketingRepository ticketingRepository, TicketService ticketService,
		TicketRepository ticketRepository) {
		this.ticketingRepository = ticketingRepository;
		this.ticketService = ticketService;
		this.ticketRepository = ticketRepository;
	}

	public List<GetAllTicketingsResultDto> getAllTicketings() {
		var ticketings = ticketingRepository.findAll()
			.stream()
			.map((ticketing) -> {
				// Todo - query로 처리해서 remainTicketStock 개수 한번에 가져오기
				var remainedTickets = ticketRepository.findByTicketingIdAndPurchaseIsNull(
					ticketing.getId());
				return GetAllTicketingsResultDto.builder().ticketingId(ticketing.getId())
					.price(ticketing.getPrice())
					.category(ticketing.getCategory())
					.location(ticketing.getLocation())
					.title(ticketing.getTitle())
					.runningMinutes(ticketing.getRunningMinutes())
					.eventTime(ticketing.getEventTime())
					.saleStart(ticketing.getSaleStart())
					.saleEnd(ticketing.getSaleEnd())
					.createdAt(ticketing.getCreatedAt())
					.remainedStock(remainedTickets.size())
					.build();
			})
			.toList();
		return ticketings;
	}

	public GetTicketingResultDto getTickting(GetTicketingCommandDto command) {
		var ticketing = ticketingRepository.getReferenceById(command.getTicketingId());
		var tickets = ticketService.listTicketByTicketingId(ticketing.getId());
		var numOfRemainedTickets = (int)tickets.stream().filter(ticket -> ticket.getPurchase() == null).count();
		return GetTicketingResultDto.builder().ticketingId(ticketing.getId())
			.price(ticketing.getPrice())
			.category(ticketing.getCategory())
			.location(ticketing.getLocation())
			.description(ticketing.getDescription())
			.title(ticketing.getTitle())
			.runningMinutes(ticketing.getRunningMinutes())
			.eventTime(ticketing.getEventTime())
			.saleStart(ticketing.getSaleStart())
			.saleEnd(ticketing.getSaleEnd())
			.createdAt(ticketing.getCreatedAt())
			.stock(tickets.size())
			.remainedStock(numOfRemainedTickets)
			.owner(ticketing.getMember().getEmail())
			.build();
	}

	public Ticketing findById(UUID ticketingId) {
		return ticketingRepository.findById(ticketingId).orElseThrow(TicketingNotFoundException::new);
	}

	@Transactional
	public Ticketing saveTicketing(Ticketing ticketing) {
		var eventTime = ticketing.getEventTime();
		var saleStart = ticketing.getSaleStart();
		var saleEnd = ticketing.getSaleEnd();

		validateTicketingMetadata(eventTime, saleStart, saleEnd);

		return ticketingRepository.save(ticketing);
	}

	@Transactional
	public void deleteTicketing(UUID ticketingId) {
		var ticketing = findById(ticketingId);
		ticketingRepository.delete(ticketing);
	}

	public void validateTicketingMetadata(LocalDateTime eventTime,
		LocalDateTime saleStart, LocalDateTime saleEnd) {
		if (eventTime == null) {
			throw new EventTimeNotValidException();
		}
		if (!isSaleDurationValid(saleStart, saleEnd)
			|| !isEventTimeAndSaleEndValid(eventTime, saleEnd)) {
			throw new SaleDurationNotValidException();
		}
	}

	private boolean isSaleDurationValid(LocalDateTime saleStart, LocalDateTime saleEnd) {
		return saleStart != null && saleEnd != null && saleEnd.isAfter(saleStart);
	}

	private boolean isEventTimeAndSaleEndValid(LocalDateTime eventTime, LocalDateTime saleEnd) {
		return eventTime.isAfter(saleEnd);
	}

	public void validateTicketingOwnership(Ticketing ticketing, String email) {
		if (!ticketing.getMember().getEmail().equals(email)) {
			throw new ModifyForNotOwnedTicketingException();
		}
		return;
	}
}
