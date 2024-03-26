package com.tiketeer.Tiketeer.domain.member.controller.dto;

import java.util.UUID;

import com.tiketeer.Tiketeer.domain.member.annotation.ValidPassword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SetPasswordWithOtpRequestDto {
	@NotNull
	private final UUID otp;

	@NotBlank
	@ValidPassword
	private final String password;

	@Builder
	public SetPasswordWithOtpRequestDto(@NotNull UUID otp, @NotBlank String password) {
		this.otp = otp;
		this.password = password;
	}
}
