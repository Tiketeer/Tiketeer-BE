package com.tiketeer.Tiketeer.domain.member.usecase.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChargePointCommandDto {
	private final UUID memberId;
	private final String email;
	private final Long pointForCharge;

	@Builder
	public ChargePointCommandDto(UUID memberId, String email, Long pointForCharge) {
		this.memberId = memberId;
		this.email = email;
		this.pointForCharge = pointForCharge;
	}
}
