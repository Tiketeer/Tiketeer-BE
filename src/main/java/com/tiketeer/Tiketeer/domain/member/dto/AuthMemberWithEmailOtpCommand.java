package com.tiketeer.Tiketeer.domain.member.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AuthMemberWithEmailOtpCommand {
	private final UUID otp;

	@Builder
	public AuthMemberWithEmailOtpCommand(UUID otp) {
		this.otp = otp;
	}
}
