package com.tiketeer.Tiketeer.domain.member.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class LoginResponseDto {
	private final String email;

	@Builder
	public LoginResponseDto(String email) {
		this.email = email;
	}

}
