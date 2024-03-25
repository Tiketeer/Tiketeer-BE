package com.tiketeer.Tiketeer.domain.member.usecase.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChargePointResultDto {
	private Long totalPoint;

	@Builder
	public ChargePointResultDto(Long totalPoint) {
		this.totalPoint = totalPoint;
	}
}
