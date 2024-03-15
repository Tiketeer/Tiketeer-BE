package com.tiketeer.Tiketeer.domain.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.member.controller.dto.AuthEmailRequestDto;
import com.tiketeer.Tiketeer.domain.member.service.MemberService;
import com.tiketeer.Tiketeer.domain.member.service.dto.AuthMemberWithEmailOtpCommandDto;

import jakarta.validation.Valid;

@RestController
public class AuthController {
	private final MemberService memberService;

	@Autowired
	public AuthController(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping(path = "/auth/otp/email")
	public ResponseEntity authEmailWithOtp(@Valid @RequestBody AuthEmailRequestDto request) {
		memberService.authMemberWithEmailOtp(
			AuthMemberWithEmailOtpCommandDto
				.builder()
				.otp(request.getOtp())
				.build()
		);
		return ResponseEntity.ok().build();
	}
}
