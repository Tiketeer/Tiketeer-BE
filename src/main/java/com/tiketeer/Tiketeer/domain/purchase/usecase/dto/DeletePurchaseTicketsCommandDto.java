package com.tiketeer.Tiketeer.domain.purchase.usecase.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DeletePurchaseTicketsCommandDto {
	private final String memberEmail;
	private final List<UUID> ticketIds;
	private final UUID purchaseId;
	private LocalDateTime commandCreatedAt = LocalDateTime.now();

	@Builder
	public DeletePurchaseTicketsCommandDto(String memberEmail, List<UUID> ticketIds,
		UUID purchaseId,
		LocalDateTime commandCreatedAt) {
		this.memberEmail = memberEmail;
		this.ticketIds = ticketIds;
		this.purchaseId = purchaseId;
		if (commandCreatedAt != null) {
			this.commandCreatedAt = commandCreatedAt;
		}
	}
}
