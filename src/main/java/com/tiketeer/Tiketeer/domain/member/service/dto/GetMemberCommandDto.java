package com.tiketeer.Tiketeer.domain.member.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GetMemberCommandDto {
	private final String memberEmail;

	@Builder
	public GetMemberCommandDto(String memberEmail) {
		this.memberEmail = memberEmail;
	}
}
