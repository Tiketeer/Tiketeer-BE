package com.tiketeer.Tiketeer.domain.member.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(force = true)
public class GetMemberTicketingSalesResultDto {

	@Builder
	public GetMemberTicketingSalesResultDto(
		UUID ticketingId,
		String title,
		String description,
		String location,
		LocalDateTime eventTime,
		LocalDateTime saleStart,
		LocalDateTime saleEnd,
		long stock,
		long remainStock,
		LocalDateTime createdAt,
		String category,
		int runningMinutes,
		long price
	) {
		this.ticketingId = ticketingId;
		this.title = title;
		this.description = description;
		this.location = location;
		this.eventTime = eventTime;
		this.saleStart = saleStart;
		this.saleEnd = saleEnd;
		this.stock = stock;
		this.remainStock = remainStock;
		this.createdAt = createdAt;
		this.category = category;
		this.runningMinutes = runningMinutes;
		this.price = price;
	}

	private final UUID ticketingId;
	private final String title;
	private final String description;
	private final String location;
	private final LocalDateTime eventTime;
	private final LocalDateTime saleStart;
	private final LocalDateTime saleEnd;
	private final long stock;
	private final long remainStock;
	private final LocalDateTime createdAt;
	private final String category;
	private final int runningMinutes;
	private final long price;

}
