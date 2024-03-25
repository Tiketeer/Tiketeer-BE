package com.tiketeer.Tiketeer.domain.ticketing.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdateTicketingCommandDto {
	private final UUID ticketingId;
	private final String email;
	private final String title;
	private final String description;
	private final String location;
	private final String category;
	private final Integer runningMinutes;
	private final Integer stock;
	private final Long price;
	private final LocalDateTime eventTime;
	private final LocalDateTime saleStart;
	private final LocalDateTime saleEnd;
	private LocalDateTime commandCreatedAt = LocalDateTime.now();

	@Builder
	public UpdateTicketingCommandDto(
		UUID ticketingId,
		String email,
		String title,
		String description,
		String location,
		String category,
		Integer runningMinutes,
		Integer stock,
		Long price,
		LocalDateTime eventTime,
		LocalDateTime saleStart,
		LocalDateTime saleEnd,
		LocalDateTime commandCreatedAt) {
		this.ticketingId = ticketingId;
		this.email = email;
		this.title = title;
		this.description = description;
		this.location = location;
		this.category = category;
		this.runningMinutes = runningMinutes;
		this.stock = stock;
		this.price = price;
		this.eventTime = eventTime;
		this.saleStart = saleStart;
		this.saleEnd = saleEnd;
		if (commandCreatedAt != null) {
			this.commandCreatedAt = commandCreatedAt;
		}
	}
}
