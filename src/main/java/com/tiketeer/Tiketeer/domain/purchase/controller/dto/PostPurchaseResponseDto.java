package com.tiketeer.Tiketeer.domain.purchase.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tiketeer.Tiketeer.domain.purchase.service.dto.CreatePurchaseResultDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PostPurchaseResponseDto {
	private final UUID purchaseId;
	private LocalDateTime createdAt;

	@Builder
	PostPurchaseResponseDto(UUID purchaseId, LocalDateTime createdAt) {
		this.purchaseId = purchaseId;
		this.createdAt = createdAt;
	}

	public static PostPurchaseResponseDto converFromDto(CreatePurchaseResultDto result) {
		return PostPurchaseResponseDto.builder()
			.purchaseId((result.getPurchaseId()))
			.createdAt(result.getCreatedAt())
			.build();
	}

}
