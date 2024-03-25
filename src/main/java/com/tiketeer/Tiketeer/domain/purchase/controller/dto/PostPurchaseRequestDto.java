package com.tiketeer.Tiketeer.domain.purchase.controller.dto;

import java.util.UUID;

import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseCommandDto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(force = true)
public class PostPurchaseRequestDto {
	@NotNull
	private final UUID ticketingId;

	@NotNull
	private final Integer count;

	@Builder
	public PostPurchaseRequestDto(@NotNull UUID ticketingId,
		@NotNull Integer count) {
		this.ticketingId = ticketingId;
		this.count = count;

	}

	public CreatePurchaseCommandDto convertToDto(String memberEmail) {
		return CreatePurchaseCommandDto.builder().memberEmail(memberEmail)
			.ticketingId(this.ticketingId)
			.count(this.count)
			.build();
	}
}
