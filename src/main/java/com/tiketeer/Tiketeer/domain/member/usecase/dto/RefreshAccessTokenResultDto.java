package com.tiketeer.Tiketeer.domain.member.usecase.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class RefreshAccessTokenResultDto {
	private final String accessToken;

	@Builder
	public RefreshAccessTokenResultDto(String accessToken) {
		this.accessToken = accessToken;
	}

	public static RefreshAccessTokenResultDto toDto(String accessToken) {
		return new RefreshAccessTokenResultDto(accessToken);
	}
}
