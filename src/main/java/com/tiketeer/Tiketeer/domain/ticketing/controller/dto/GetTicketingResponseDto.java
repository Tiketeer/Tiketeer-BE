package com.tiketeer.Tiketeer.domain.ticketing.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tiketeer.Tiketeer.domain.ticketing.service.dto.GetTicketingResultDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class GetTicketingResponseDto {
	private final UUID ticketingId;
	private final String title;
	private final String description;
	private final String location;
	private final String category;
	private final Integer runningMinutes;
	private final Integer stock;
	private final Integer remainedStock;
	private final Long price;
	private final LocalDateTime eventTime;
	private final LocalDateTime saleStart;
	private final LocalDateTime saleEnd;
	private final LocalDateTime createdAt;
	private final String owner;

	@Builder
	public GetTicketingResponseDto(UUID ticketingId, String title,
		String description,
		String location,
		String category,
		Integer runningMinutes,
		Integer stock,
		Integer remainedStock,
		Long price,
		LocalDateTime eventTime,
		LocalDateTime saleStart,
		LocalDateTime saleEnd, LocalDateTime createdAt, String owner) {
		this.ticketingId = ticketingId;
		this.title = title;
		this.description = description;
		this.location = location;
		this.category = category;
		this.runningMinutes = runningMinutes;
		this.stock = stock;
		this.remainedStock = remainedStock;
		this.price = price;
		this.eventTime = eventTime;
		this.saleStart = saleStart;
		this.saleEnd = saleEnd;
		this.createdAt = createdAt;
		this.owner = owner;
	}

	public static GetTicketingResponseDto convertFromDto(GetTicketingResultDto dto) {
		return GetTicketingResponseDto.builder()
			.ticketingId(dto.getTicketingId())
			.title(dto.getTitle())
			.description(dto.getDescription())
			.location(dto.getLocation())
			.category(dto.getCategory())
			.runningMinutes(dto.getRunningMinutes())
			.stock(dto.getStock())
			.remainedStock(dto.getRemainedStock())
			.price(dto.getPrice())
			.eventTime(dto.getEventTime())
			.saleStart(dto.getSaleStart())
			.saleEnd(dto.getSaleEnd())
			.createdAt(dto.getCreatedAt())
			.owner(dto.getOwner())
			.build();
	}
}
