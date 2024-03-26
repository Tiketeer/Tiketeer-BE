package com.tiketeer.Tiketeer.domain.member.controller.dto;

import java.time.LocalDateTime;

import com.tiketeer.Tiketeer.domain.member.usecase.dto.GetMemberResultDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(force = true)
public class GetMemberResponseDto {
	private String email;
	private LocalDateTime createdAt;
	private Long point;
	private String profileUrl;

	@Builder
	public GetMemberResponseDto(String email, LocalDateTime createdAt, Long point, String profileUrl) {
		this.email = email;
		this.createdAt = createdAt;
		this.point = point;
		this.profileUrl = profileUrl;
	}

	public static GetMemberResponseDto convertFromDto(GetMemberResultDto dto) {
		return GetMemberResponseDto.builder()
			.email(dto.getEmail())
			.createdAt(dto.getCreatedAt())
			.point(dto.getPoint())
			.profileUrl(dto.getProfileUrl())
			.build();
	}
}
