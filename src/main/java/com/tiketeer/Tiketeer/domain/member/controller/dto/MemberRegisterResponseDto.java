package com.tiketeer.Tiketeer.domain.member.controller.dto;

import java.util.UUID;

import com.tiketeer.Tiketeer.domain.member.usecase.dto.MemberRegisterResultDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class MemberRegisterResponseDto {
	private final UUID memberId;

	@Builder
	public MemberRegisterResponseDto(UUID memberId) {
		this.memberId = memberId;
	}

	public static MemberRegisterResponseDto toDto(MemberRegisterResultDto member) {
		return new MemberRegisterResponseDto(member.getMemberId());
	}
}
