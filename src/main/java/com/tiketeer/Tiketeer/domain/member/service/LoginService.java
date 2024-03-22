package com.tiketeer.Tiketeer.domain.member.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.auth.jwt.AccessTokenPayload;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.auth.jwt.RefreshTokenPayload;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.RefreshToken;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidLoginException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.repository.RefreshTokenRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.LoginCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.LoginResultDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Slf4j
public class LoginService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final RefreshTokenRepository refreshTokenRepository;

	@Value("${jwt.access-key-expiration-ms}")
	private long accessKeyExpirationInMs;

	@Value("${jwt.refresh-key-expiration-ms}")
	private long refreshKeyExpirationInMs;

	public LoginService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
		RefreshTokenRepository refreshTokenRepository) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.refreshTokenRepository = refreshTokenRepository;
	}

	@Transactional
	public LoginResultDto login(LoginCommandDto command) {

		Member member = getValidatedMember(command.getEmail(), command.getPassword());

		refreshTokenRepository.findByMember(member).ifPresent(refreshTokenRepository::delete);
		RefreshToken saved = refreshTokenRepository.save(RefreshToken.builder()
			.member(member)
			.expiredAt(LocalDateTime.now().plus(Duration.ofMillis(refreshKeyExpirationInMs)))
			.build());

		String accessToken = generateAccessToken(member);
		String refreshToken = generateRefreshToken(saved.getId());

		return new LoginResultDto(accessToken, refreshToken, member);
	}

	private String generateAccessToken(Member member) {
		AccessTokenPayload jwtPayload = new AccessTokenPayload(member.getEmail(), member.getRole().getName(),
			new Date());
		return jwtService.createAccessToken(jwtPayload);
	}

	private String generateRefreshToken(UUID tokenId) {
		RefreshTokenPayload refreshTokenPayload = new RefreshTokenPayload(tokenId.toString(), new Date());
		return jwtService.createRefreshToken(refreshTokenPayload);
	}

	private Member getValidatedMember(String email, String password) {
		Member member = memberRepository.findByEmail(email).orElse(null);

		if (member == null) {
			log.warn("존재하지 않는 회원인 {}으로 로그인 시도", email);
			throw new InvalidLoginException();
		}

		if (!passwordEncoder.matches(password, member.getPassword())) {
			log.warn("잘못된 비밀번호인 {}으로 로그인 시도", password);
			throw new InvalidLoginException();
		}
		return member;
	}

}
