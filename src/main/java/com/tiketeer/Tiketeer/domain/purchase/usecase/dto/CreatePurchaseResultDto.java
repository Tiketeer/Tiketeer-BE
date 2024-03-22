package com.tiketeer.Tiketeer.domain.purchase.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreatePurchaseResultDto {
	private final UUID purchaseId;
	private LocalDateTime createdAt;

	@Builder
	CreatePurchaseResultDto(UUID purchaseId, LocalDateTime createdAt) {
		this.purchaseId = purchaseId;
		this.createdAt = createdAt;
	}

}
