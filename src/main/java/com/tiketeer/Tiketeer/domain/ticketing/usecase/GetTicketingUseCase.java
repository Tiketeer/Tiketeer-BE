package com.tiketeer.Tiketeer.domain.ticketing.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticket.service.TicketService;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.GetTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.GetTicketingResultDto;

@Service
@Transactional(readOnly = true)
public class GetTicketingUseCase {
	private final TicketingRepository ticketingRepository;
	private final TicketService ticketService;

	@Autowired
	public GetTicketingUseCase(TicketingRepository ticketingRepository, TicketService ticketService) {
		this.ticketingRepository = ticketingRepository;
		this.ticketService = ticketService;
	}

	public GetTicketingResultDto getTicketing(GetTicketingCommandDto command) {
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
}
