package com.tiketeer.Tiketeer.domain.member.usecase.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class InitMemberPasswordWithOtpCommandDto {
	private final UUID otp;
	private final String password;

	@Builder
	public InitMemberPasswordWithOtpCommandDto(UUID otp, String password) {
		this.otp = otp;
		this.password = password;
	}
}
