package com.tiketeer.Tiketeer.domain.member.controller.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AuthEmailRequestDto {
	@NotNull
	private final UUID otp;

	@Builder
	public AuthEmailRequestDto(@NotNull UUID otp) {
		this.otp = otp;
	}
}
