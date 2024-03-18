package com.tiketeer.Tiketeer.domain.member.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChargePointResponseDto {
	private Long totalPoint;

	@Builder
	public ChargePointResponseDto(Long totalPoint) {
		this.totalPoint = totalPoint;
	}
}
