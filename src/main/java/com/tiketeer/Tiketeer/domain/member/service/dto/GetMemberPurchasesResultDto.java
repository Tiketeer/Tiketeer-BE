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
public class GetMemberPurchasesResultDto {
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
	public GetMemberPurchasesResultDto(UUID purchaseId, UUID ticketingId, String title, String location,
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
}
