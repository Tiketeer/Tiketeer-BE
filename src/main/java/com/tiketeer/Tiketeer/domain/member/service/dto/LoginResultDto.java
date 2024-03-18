package com.tiketeer.Tiketeer.domain.member.service.dto;

import com.tiketeer.Tiketeer.domain.member.controller.dto.LoginResponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class LoginResultDto {

	private String accessToken;

	@Builder
	public LoginResultDto(String accessToken) {
		this.accessToken = accessToken;
	}

	public static LoginResponseDto convertFromDto(LoginResultDto dto) {
		return new LoginResponseDto(dto.getAccessToken());
	}
}
