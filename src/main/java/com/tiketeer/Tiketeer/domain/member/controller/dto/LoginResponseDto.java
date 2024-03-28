package com.tiketeer.Tiketeer.domain.member.controller.dto;

import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class LoginResponseDto {
	private final String email;
	private final RoleEnum roleEnum;

	@Builder
	public LoginResponseDto(String email, RoleEnum roleEnum) {
		this.email = email;
		this.roleEnum = roleEnum;
	}

}
