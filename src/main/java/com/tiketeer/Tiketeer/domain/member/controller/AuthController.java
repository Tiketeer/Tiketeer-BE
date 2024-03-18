package com.tiketeer.Tiketeer.domain.member.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.auth.constant.JwtMetadata;
import com.tiketeer.Tiketeer.domain.member.controller.dto.LoginRequestDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.LoginResponseDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.SetPasswordWithOtpRequestDto;
import com.tiketeer.Tiketeer.domain.member.service.LoginService;
import com.tiketeer.Tiketeer.domain.member.service.MemberService;
import com.tiketeer.Tiketeer.domain.member.service.dto.InitMemberPasswordWithOtpCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.LoginResultDto;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AuthController {
	private final MemberService memberService;
	private final LoginService loginService;

	@Value("${jwt.access-key-expiration-ms}")
	private long accessKeyExpirationInMs;

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
	public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody final LoginRequestDto request) {
		LoginResultDto loginResult = loginService.login(request.toCommand());
		log.info("user {} logged in", request.getEmail());

		var responseBody = ApiResponse.wrap(LoginResultDto.convertFromDto(loginResult));

		return ResponseEntity.status(HttpStatus.OK)
			.header(HttpHeaders.SET_COOKIE, createCookie(loginResult).toString())
			.body(responseBody);

	}

	private ResponseCookie createCookie(LoginResultDto loginResult) {
		return ResponseCookie.from(JwtMetadata.ACCESS_TOKEN, loginResult.getAccessToken())
			.httpOnly(true)
			.secure(true)
			.maxAge(Duration.ofMillis(accessKeyExpirationInMs))
			.build();
	}

}
