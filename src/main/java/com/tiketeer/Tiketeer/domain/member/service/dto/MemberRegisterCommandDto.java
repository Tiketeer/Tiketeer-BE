package com.tiketeer.Tiketeer.domain.member.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class MemberRegisterCommandDto {
	private final String email;

	private final Boolean isSeller;

	@Builder
	public MemberRegisterCommandDto(String email, Boolean isSeller) {
		this.email = email;
		this.isSeller = isSeller;
	}
}
