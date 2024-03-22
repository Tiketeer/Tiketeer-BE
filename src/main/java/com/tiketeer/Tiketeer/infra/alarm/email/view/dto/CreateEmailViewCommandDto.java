package com.tiketeer.Tiketeer.infra.alarm.email.view.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class CreateEmailViewCommandDto {
	private UUID otp;
	private String email;

	@Builder
	public CreateEmailViewCommandDto(UUID otp, String email) {
		this.otp = otp;
		this.email = email;
	}
}
