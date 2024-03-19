package com.tiketeer.Tiketeer.domain.member.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class RefreshAccessTokenCommandDto {
	private final String refreshToken;

	@Builder
	public RefreshAccessTokenCommandDto(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
