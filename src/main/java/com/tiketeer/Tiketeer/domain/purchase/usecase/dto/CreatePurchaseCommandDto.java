package com.tiketeer.Tiketeer.domain.purchase.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreatePurchaseCommandDto {
	private final String memberEmail;
	private final UUID ticketingId;
	private final Integer count;
	private LocalDateTime commandCreatedAt = LocalDateTime.now();

	@Builder
	public CreatePurchaseCommandDto(String memberEmail, UUID ticketingId, Integer count,
		LocalDateTime commandCreatedAt) {
		this.memberEmail = memberEmail;
		this.ticketingId = ticketingId;
		this.count = count;
		if (commandCreatedAt != null) {
			this.commandCreatedAt = commandCreatedAt;
		}
	}
}
