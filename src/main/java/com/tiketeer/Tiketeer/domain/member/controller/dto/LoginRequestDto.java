package com.tiketeer.Tiketeer.domain.member.controller.dto;

import com.tiketeer.Tiketeer.domain.member.service.dto.LoginCommandDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class LoginRequestDto {
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@NotBlank(message = "이메일은 비워둘 수 없습니다.")
	private final String email;

	//todo : 비밀번호 제약조건 (최소길이, 특수문자 필요시 추가)
	@NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
	private final String password;

	@Builder
	public LoginRequestDto(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public LoginCommandDto toCommand() {
		return new LoginCommandDto(email, password);
	}
}
