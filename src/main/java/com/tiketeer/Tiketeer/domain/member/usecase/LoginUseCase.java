package com.tiketeer.Tiketeer.domain.member.usecase;

import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.auth.jwt.JwtPayload;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidLoginException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.service.LoginService;
import com.tiketeer.Tiketeer.domain.member.service.dto.LoginCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.LoginResultDto;

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
