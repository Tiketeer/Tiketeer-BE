package com.tiketeer.Tiketeer.domain.member.service.dto;

import java.util.UUID;

import com.tiketeer.Tiketeer.domain.member.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class MemberRegisterResultDto {
	private final UUID memberId;

	@Builder
	public MemberRegisterResultDto(UUID memberId) {
		this.memberId = memberId;
	}

	public static MemberRegisterResultDto toDto(Member member) {
		return new MemberRegisterResultDto(member.getId());
	}
}
