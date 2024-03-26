package com.tiketeer.Tiketeer.testhelper.dto;

import com.tiketeer.Tiketeer.domain.member.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class TestLoginResultDto {
	private final String accessToken;
	private final String refreshToken;
	private final Member member;

	@Builder
	public TestLoginResultDto(String accessToken, String refreshToken, Member member) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.member = member;
	}
}
