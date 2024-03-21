package com.tiketeer.Tiketeer.domain.member.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tiketeer.Tiketeer.domain.member.service.dto.GetMemberPurchasesResultDto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GetMemberPurchasesResponseDto {
	private UUID purchaseId;
	private UUID ticketingId;
	private String title;
	private String location;
	private LocalDateTime eventTime;
	private LocalDateTime saleStart;
	private LocalDateTime saleEnd;
	private LocalDateTime createdAt;
	private String category;
	private Long price;
	private Long count;

	@Builder
	public GetMemberPurchasesResponseDto(UUID purchaseId, UUID ticketingId, String title, String location,
		LocalDateTime eventTime, LocalDateTime saleStart, LocalDateTime saleEnd, LocalDateTime createdAt,
		String category, Long price, Long count) {
		this.purchaseId = purchaseId;
		this.ticketingId = ticketingId;
		this.title = title;
		this.location = location;
		this.eventTime = eventTime;
		this.saleStart = saleStart;
		this.saleEnd = saleEnd;
		this.createdAt = createdAt;
		this.category = category;
		this.price = price;
		this.count = count;
	}

	public static GetMemberPurchasesResponseDto convertFromDto(GetMemberPurchasesResultDto dto) {
		return GetMemberPurchasesResponseDto.builder()
			.purchaseId(dto.getPurchaseId())
			.ticketingId(dto.getTicketingId())
			.title(dto.getTitle())
			.location(dto.getLocation())
			.eventTime(dto.getEventTime())
			.saleStart(dto.getSaleStart())
			.saleEnd(dto.getSaleEnd())
			.createdAt(dto.getCreatedAt())
			.category(dto.getCategory())
			.price(dto.getPrice())
			.count(dto.getCount())
			.build();
	}
}
