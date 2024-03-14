package com.tiketeer.Tiketeer.domain.ticketing.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateTicketingCommand {
	private String memberEmail;
	private String title;
	private String description;
	private String location;
	private String category;
	private Integer runningMinutes;
	private Integer stock;
	private Long price;
	private LocalDateTime eventTime;
	private LocalDateTime saleStart;
	private LocalDateTime saleEnd;

	@Builder
	public CreateTicketingCommand(
		String memberEmail,
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
		this.memberEmail = memberEmail;
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
