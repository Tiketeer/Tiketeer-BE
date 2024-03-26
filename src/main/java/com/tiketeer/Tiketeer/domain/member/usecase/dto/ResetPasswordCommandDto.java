package com.tiketeer.Tiketeer.domain.member.usecase.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResetPasswordCommandDto {

	private final UUID otp;

	private final String newPassword;

	@Builder
	public ResetPasswordCommandDto(UUID otp, String newPassword) {
		this.otp = otp;
		this.newPassword = newPassword;
	}

}