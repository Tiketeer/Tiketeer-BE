package com.tiketeer.Tiketeer.domain.ticketing.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.GetAllTicketingsResultDto;

@Service
@Transactional(readOnly = true)
public class GetAllTicketingsUseCase {
	private final TicketingRepository ticketingRepository;
	private final TicketRepository ticketRepository;

	@Autowired
	public GetAllTicketingsUseCase(TicketingRepository ticketingRepository, TicketRepository ticketRepository) {
		this.ticketingRepository = ticketingRepository;
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
}
