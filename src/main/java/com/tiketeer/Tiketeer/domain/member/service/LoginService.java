package com.tiketeer.Tiketeer.domain.member.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.auth.constant.JwtMetadata;
import com.tiketeer.Tiketeer.auth.jwt.JwtPayload;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidLoginException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.GenerateCookieCommand;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Slf4j
public class LoginService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	private final JwtService jwtService;

	@Value("${jwt.access-key-expiration-ms}")
	private long accessKeyExpirationInMs;

	public LoginService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public Cookie generateCookie(GenerateCookieCommand command) {
		Member member = memberRepository.findByEmail(command.getEmail()).orElse(null);

		if (member == null) {
			log.warn("존재하지 않는 회원입니다");
			throw new InvalidLoginException();
		}

		if (!passwordEncoder.matches(command.getPassword(), member.getPassword())) {
			log.warn("비밀번호가 일치하지 않습니다");
			throw new InvalidLoginException();
		}

		String accessToken = generateToken(member);
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, accessToken);
		cookie.setMaxAge((int)accessKeyExpirationInMs);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setSecure(true);

		return cookie;
	}

	private String generateToken(Member member) {
		JwtPayload jwtPayload = new JwtPayload(member.getEmail(), member.getRole().getName(), new Date());
		return jwtService.createToken(jwtPayload);
	}

}
