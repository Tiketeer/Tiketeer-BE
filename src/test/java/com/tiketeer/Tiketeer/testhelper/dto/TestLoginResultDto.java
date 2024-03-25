package com.tiketeer.Tiketeer.testhelper.dto;

import com.tiketeer.Tiketeer.domain.member.Member;

public class TestLoginResultDto {
	private String accessToken;
	private String refreshToken;
	private Member member;

	public TestLoginResultDto() {
	}

	public TestLoginResultDto(String accessToken, String refreshToken, Member member) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.member = member;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public Member getMember() {
		return member;
	}
}
