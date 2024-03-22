package com.tiketeer.Tiketeer.domain.member.usecase.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GetMemberResultDto {
	private String email;
	private LocalDateTime createdAt;
	private Long point;
	private String profileUrl;

	@Builder
	public GetMemberResultDto(String email, LocalDateTime createdAt, Long point, String profileUrl) {
		this.email = email;
		this.createdAt = createdAt;
		this.point = point;
		this.profileUrl = profileUrl;
	}
}
