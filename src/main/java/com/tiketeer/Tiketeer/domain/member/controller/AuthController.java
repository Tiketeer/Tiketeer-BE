package com.tiketeer.Tiketeer.domain.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.member.controller.dto.LoginRequestDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.SetPasswordWithOtpRequestDto;
import com.tiketeer.Tiketeer.domain.member.service.LoginService;
import com.tiketeer.Tiketeer.domain.member.service.MemberService;
import com.tiketeer.Tiketeer.domain.member.service.dto.InitMemberPasswordWithOtpCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.LoginCommandDto;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class AuthController {
	private final MemberService memberService;
	private final LoginService loginService;

	@Autowired
	public AuthController(MemberService memberService, LoginService loginService) {
		this.memberService = memberService;
		this.loginService = loginService;
	}

	@PostMapping(path = "/auth/otp/email")
	public ResponseEntity setPasswordWithOtp(@Valid @RequestBody SetPasswordWithOtpRequestDto request) {
		memberService.initPasswordWithOtp(
			InitMemberPasswordWithOtpCommandDto
				.builder()
				.otp(request.getOtp())
				.password(request.getPassword())
				.build()
		);
		return ResponseEntity.ok().build();
	}

	@PostMapping(path = "/auth/login")
	public ResponseEntity login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response) {
		loginService.login(
			LoginCommandDto
				.builder()
				.email(request.getEmail())
				.password(request.getPassword())
				.build(),
			response
		);

		return ResponseEntity.ok().build();
	}

}
