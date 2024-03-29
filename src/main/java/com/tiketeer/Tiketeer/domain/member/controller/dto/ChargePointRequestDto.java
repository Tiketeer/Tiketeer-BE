package com.tiketeer.Tiketeer.domain.member.controller.dto;

import java.util.UUID;

import com.tiketeer.Tiketeer.domain.member.usecase.dto.ChargePointCommandDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(force = true)
public class ChargePointRequestDto {
	private final Long pointForCharge;

	@Builder
	public ChargePointRequestDto(Long pointForCharge) {
		this.pointForCharge = pointForCharge;
	}

	public ChargePointCommandDto convertToCommandDto(String memberId, String email) {
		return ChargePointCommandDto.builder()
			.memberId(UUID.fromString(memberId))
			.email(email)
			.pointForCharge(pointForCharge)
			.build();
	}
}
