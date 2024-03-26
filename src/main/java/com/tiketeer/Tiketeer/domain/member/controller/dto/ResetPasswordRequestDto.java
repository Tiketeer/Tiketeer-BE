package com.tiketeer.Tiketeer.domain.member.controller.dto;

import java.util.UUID;

import com.tiketeer.Tiketeer.domain.member.annotation.ValidPassword;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.ResetPasswordCommandDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(force = true)
public class ResetPasswordRequestDto {

	@NotNull
	private UUID otp;

	@NotBlank
	@ValidPassword
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
