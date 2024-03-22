package com.tiketeer.Tiketeer.domain.member.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.service.LoginService;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.LoginCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.LoginResultDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class LoginUseCase {
	private final LoginService loginService;

	public LoginUseCase(LoginService loginService) {
		this.loginService = loginService;
	}

	@Transactional
	public LoginResultDto login(LoginCommandDto command) {
		return loginService.login(command);
	}
}
