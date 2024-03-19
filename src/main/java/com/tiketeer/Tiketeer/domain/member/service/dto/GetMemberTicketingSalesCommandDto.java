package com.tiketeer.Tiketeer.domain.member.service.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GetMemberTicketingSalesCommandDto {

	private final UUID memberId;
	private final String email;

	@Builder
	public GetMemberTicketingSalesCommandDto(UUID memberId, String email) {
		this.memberId = memberId;
		this.email = email;
	}
}
