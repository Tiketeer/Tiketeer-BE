package com.tiketeer.Tiketeer.domain.member.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class LoginCommandDto {
	private final String email;

	//todo : 비밀번호 제약조건 (최소길이, 특수문자 필요시 추가)
	private final String password;

	@Builder
	public LoginCommandDto(String email, String password) {
		this.email = email;
		this.password = password;
	}
}
