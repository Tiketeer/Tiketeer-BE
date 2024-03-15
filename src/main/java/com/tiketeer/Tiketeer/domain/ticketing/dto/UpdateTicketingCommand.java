package com.tiketeer.Tiketeer.domain.ticketing.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UpdateTicketingCommand {
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

	@Builder
	public UpdateTicketingCommand(
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
		LocalDateTime saleEnd) {
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
	}
}
