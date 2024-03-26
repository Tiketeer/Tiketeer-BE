package com.tiketeer.Tiketeer.domain.purchase.controller.dto;

import java.util.List;
import java.util.UUID;

import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.DeletePurchaseTicketsCommandDto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DeletePurchaseTicketsRequestDto {
	@NotNull
	private final List<UUID> ticketIds;

	@NotNull
	private final UUID purchaseId;

	@Builder
	public DeletePurchaseTicketsRequestDto(@NotNull List<UUID> ticketIds, @NotNull UUID purchaseId) {
		this.ticketIds = ticketIds;
		this.purchaseId = purchaseId;
	}

	public DeletePurchaseTicketsCommandDto convertToDto(String memberEmail, UUID purchaseId) {
		return DeletePurchaseTicketsCommandDto.builder().memberEmail(memberEmail)
			.ticketIds(this.ticketIds)
			.purchaseId(purchaseId)
			.build();
	}
}
