package com.tiketeer.Tiketeer.domain.ticketing.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.GetAllTicketingsResultDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class GetAllTicketingsResponseDto {
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
	public GetAllTicketingsResponseDto(UUID ticketingId, String title,
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

	public static GetAllTicketingsResponseDto convertFromDto(GetAllTicketingsResultDto dto) {
		return GetAllTicketingsResponseDto.builder()
			.ticketingId(dto.getTicketingId())
			.title(dto.getTitle())
			.location(dto.getLocation())
			.category(dto.getCategory())
			.runningMinutes(dto.getRunningMinutes())
			.remainedStock(dto.getRemainedStock())
			.price(dto.getPrice())
			.eventTime(dto.getEventTime())
			.saleStart(dto.getSaleStart())
			.saleEnd(dto.getSaleEnd()).createdAt(dto.getCreatedAt())
			.build();
	}
}
