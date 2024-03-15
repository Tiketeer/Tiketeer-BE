package com.tiketeer.Tiketeer.domain.member.service.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AuthMemberWithEmailOtpCommandDto {
	private final UUID otp;

	@Builder
	public AuthMemberWithEmailOtpCommandDto(UUID otp) {
		this.otp = otp;
	}
}
