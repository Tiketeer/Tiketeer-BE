package com.tiketeer.Tiketeer.domain.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.member.controller.dto.SetPasswordWithOtpRequestDto;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidTokenException;
import com.tiketeer.Tiketeer.domain.member.service.MemberService;
import com.tiketeer.Tiketeer.domain.member.service.dto.InitMemberPasswordWithOtpCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.RefreshAccessTokenCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.RefreshAccessTokenResultDto;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private final MemberService memberService;

	@Autowired
	public AuthController(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping(path = "/otp/email")
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

	@PostMapping(path = "/refresh")
	public ResponseEntity refreshAccessToken(@RequestHeader("Authorization") String authorizationHeader,
		HttpServletResponse response) {
		String refreshToken = getRefreshToken(authorizationHeader);

		RefreshAccessTokenResultDto refreshAccessTokenResultDto = memberService.refreshAccessToken(
			RefreshAccessTokenCommandDto.builder().refreshToken(refreshToken).build());

		Cookie cookie = setCookie("accessToken", refreshAccessTokenResultDto.getAccessToken());
		response.addCookie(cookie);

		return ResponseEntity.ok().build();
	}

	private String getRefreshToken(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}

		throw new InvalidTokenException();
	}

	private Cookie setCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(5 * 60);

		return cookie;
	}
}
