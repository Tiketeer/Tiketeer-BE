package com.tiketeer.Tiketeer.domain.member.usecase.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GetMemberPurchasesCommandDto {
	private final String memberEmail;

	@Builder
	public GetMemberPurchasesCommandDto(String memberEmail) {
		this.memberEmail = memberEmail;
	}
}
