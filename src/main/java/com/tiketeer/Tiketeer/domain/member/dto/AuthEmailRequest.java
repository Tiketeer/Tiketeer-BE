package com.tiketeer.Tiketeer.domain.member.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AuthEmailRequest {
	@NotNull
	private final UUID otp;

	@Builder
	public AuthEmailRequest(@NotNull UUID otp) {
		this.otp = otp;
	}
}
