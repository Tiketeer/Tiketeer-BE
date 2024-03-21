package com.tiketeer.Tiketeer.domain.ticketing.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class GetAllTicketingsResultDto {
	private final UUID ticketingId;
	private final String title;
	private final String location;
	private final String category;
	private final Integer runningMinutes;
	private final Integer remainedStock;
	private final Long price;
	private final LocalDateTime eventTime;
	private final LocalDateTime saleStart;
	private final LocalDateTime saleEnd;
	private final LocalDateTime createdAt;

	@Builder
	public GetAllTicketingsResultDto(UUID ticketingId, String title,
		String location,
		String category,
		Integer runningMinutes,
		Integer remainedStock,
		Long price,
		LocalDateTime eventTime,
		LocalDateTime saleStart,
		LocalDateTime saleEnd, LocalDateTime createdAt) {
		this.ticketingId = ticketingId;
		this.title = title;
		this.location = location;
		this.category = category;
		this.runningMinutes = runningMinutes;
		this.remainedStock = remainedStock;
		this.price = price;
		this.eventTime = eventTime;
		this.saleStart = saleStart;
		this.saleEnd = saleEnd;
		this.createdAt = createdAt;
	}
}
