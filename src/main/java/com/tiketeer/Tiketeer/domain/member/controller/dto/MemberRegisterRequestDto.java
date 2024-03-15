package com.tiketeer.Tiketeer.domain.member.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class MemberRegisterRequestDto {
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@NotBlank(message = "이메일은 비워둘 수 없습니다.")
	private final String email;

	@NotNull(message = "판매자 여부를 선택해야 합니다.")
	private final Boolean isSeller;

	@Builder
	public MemberRegisterRequestDto(String email, Boolean isSeller) {
		this.email = email;
		this.isSeller = isSeller;
	}
}
