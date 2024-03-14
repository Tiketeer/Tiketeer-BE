package com.tiketeer.Tiketeer.domain.ticketing.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PostTicketingRequest {
	@NotNull
	private final String title;

	private final String description;

	@NotNull
	private final String location;

	@NotNull
	private final String category;

	@NotNull
	private final Integer runningMinutes;

	@NotNull
	private final Integer stock;

	@NotNull
	private final Long price;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private final LocalDateTime eventTime;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private final LocalDateTime saleStart;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private final LocalDateTime saleEnd;

	@Builder
	public PostTicketingRequest(
		@NotNull String title,
		String description,
		@NotNull String location,
		@NotNull String category,
		@NotNull Integer runningMinutes,
		@NotNull Integer stock,
		@NotNull Long price,
		@NotNull LocalDateTime eventTime,
		@NotNull LocalDateTime saleStart,
		@NotNull LocalDateTime saleEnd) {
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

	public CreateTicketingCommand convertToDto(String memberEmail) {
		return CreateTicketingCommand.builder()
			.memberEmail(memberEmail)
			.title(title)
			.description(description)
			.location(location)
			.category(category)
			.runningMinutes(runningMinutes)
			.stock(stock)
			.price(price)
			.eventTime(eventTime)
			.saleStart(saleStart)
			.saleEnd(saleEnd)
			.build();
	}
}
