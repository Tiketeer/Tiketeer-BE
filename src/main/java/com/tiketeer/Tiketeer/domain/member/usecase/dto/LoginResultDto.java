package com.tiketeer.Tiketeer.domain.member.usecase.dto;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.controller.dto.LoginResponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class LoginResultDto {

	private String accessToken;
	private String refreshToken;
	private Member member;

	@Builder
	public LoginResultDto(String accessToken, String refreshToken, Member member) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.member = member;
	}

	public static LoginResponseDto convertFromDto(LoginResultDto dto) {
		return new LoginResponseDto(dto.member.getEmail());
	}
}
