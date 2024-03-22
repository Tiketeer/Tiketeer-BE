package com.tiketeer.Tiketeer.domain.member.controller.dto;

import java.util.UUID;

import com.tiketeer.Tiketeer.domain.member.usecase.dto.ResetPasswordCommandDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResetPasswordRequestDto {

	@NotBlank
	private UUID otp;

	@NotBlank
	private String newPassword;

	@Builder
	public ResetPasswordRequestDto(UUID otp, String newPassword) {
		this.otp = otp;
		this.newPassword = newPassword;
	}

	public ResetPasswordCommandDto toCommand() {
		return ResetPasswordCommandDto.builder()
			.otp(otp)
			.newPassword(newPassword)
			.build();
	}

}
