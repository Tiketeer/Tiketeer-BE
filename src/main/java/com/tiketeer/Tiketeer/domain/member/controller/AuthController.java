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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.auth.constant.JwtMetadata;
import com.tiketeer.Tiketeer.domain.member.controller.dto.LoginRequestDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.LoginResponseDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.SetPasswordWithOtpRequestDto;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidTokenException;
import com.tiketeer.Tiketeer.domain.member.usecase.InitPasswordWithOtpUseCase;
import com.tiketeer.Tiketeer.domain.member.usecase.LoginUseCase;
import com.tiketeer.Tiketeer.domain.member.usecase.RefreshAccessTokenUseCase;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.InitMemberPasswordWithOtpCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.LoginResultDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.RefreshAccessTokenCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.RefreshAccessTokenResultDto;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {
	private final InitPasswordWithOtpUseCase initPasswordWithOtp;
	private final RefreshAccessTokenUseCase refreshAccessToken;
	private final LoginUseCase loginUseCase;

	@Value("${jwt.access-key-expiration-ms}")
	private long accessKeyExpirationInMs;

	@Value("${server.servlet.context-path}")
	private String contextPath;

	@Autowired
	public AuthController(InitPasswordWithOtpUseCase initPasswordWithOtp, RefreshAccessTokenUseCase refreshAccessToken,
		LoginUseCase loginUseCase) {
		this.initPasswordWithOtp = initPasswordWithOtp;
		this.refreshAccessToken = refreshAccessToken;
		this.loginUseCase = loginUseCase;
	}

	@PostMapping(path = "/otp/email")
	public ResponseEntity setPasswordWithOtp(@Valid @RequestBody SetPasswordWithOtpRequestDto request) {
		initPasswordWithOtp.init(
			InitMemberPasswordWithOtpCommandDto
				.builder()
				.otp(request.getOtp())
				.password(request.getPassword())
				.build()
		);
		return ResponseEntity.ok().build();
	}

	@PostMapping(path = "/login")
	public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody final LoginRequestDto request) {
		LoginResultDto loginResult = loginUseCase.login(request.toCommand());
		log.info("user {} logged in", request.getEmail());

		var responseBody = ApiResponse.wrap(LoginResultDto.convertFromDto(loginResult));

		return ResponseEntity.status(HttpStatus.OK)
			.header(HttpHeaders.SET_COOKIE, createCookie(loginResult.getAccessToken()).toString())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResult.getRefreshToken())
			.body(responseBody);
	}

	private ResponseCookie createCookie(String accessToken) {
		return ResponseCookie.from(JwtMetadata.ACCESS_TOKEN, accessToken)
			.httpOnly(true)
			.secure(true)
			.maxAge(Duration.ofMillis(accessKeyExpirationInMs))
			.path(contextPath)
			.sameSite("Strict")
			.build();
	}

	@PostMapping(path = "/refresh")
	public ResponseEntity refreshAccessToken(@RequestHeader("Authorization") String authorizationHeader,
		HttpServletResponse response) {
		String refreshToken = getRefreshToken(authorizationHeader);

		RefreshAccessTokenResultDto refreshAccessTokenResultDto = refreshAccessToken.refresh(
			RefreshAccessTokenCommandDto.builder().refreshToken(refreshToken).build());

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, createCookie(refreshAccessTokenResultDto.getAccessToken()).toString())
			.build();
	}

	private String getRefreshToken(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}

		throw new InvalidTokenException();
	}

	private Cookie setCookie(String key, String value, CookieOptions options) {
		Cookie cookie = new Cookie(key, value);
		if (options.httpOnly != null) {
			cookie.setHttpOnly(options.httpOnly);
		}
		if (options.path != null) {
			cookie.setPath(options.path);
		}
		if (options.maxAge != null) {
			cookie.setMaxAge(options.maxAge);
		}
		return cookie;
	}

	public record CookieOptions(Boolean httpOnly, String path, Integer maxAge) {
	}
}
