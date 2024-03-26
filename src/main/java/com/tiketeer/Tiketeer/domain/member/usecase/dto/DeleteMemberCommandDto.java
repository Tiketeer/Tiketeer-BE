package com.tiketeer.Tiketeer.domain.member.usecase.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(force = true)
public class DeleteMemberCommandDto {
	private final UUID memberId;
	private final String email;
	private LocalDateTime commandCreatedAt;

	@Builder
	public DeleteMemberCommandDto(UUID memberId, String email, LocalDateTime commandCreatedAt) {
		this.memberId = memberId;
		this.email = email;
		if (commandCreatedAt != null) {
			this.commandCreatedAt = commandCreatedAt;
		}
	}
}
