package com.tiketeer.Tiketeer.domain.member.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.auth.jwt.JwtMetadata;
import com.tiketeer.Tiketeer.auth.jwt.JwtPayload;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidLoginException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.LoginCommandDto;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

	@Transactional
	public void login(LoginCommandDto command, HttpServletResponse response) {
		Member member = memberRepository.findByEmail(command.getEmail()).orElse(null);

		if (member == null) {
			log.warn("존재하지 않는 회원입니다");
			throw new InvalidLoginException();
		}

		if (!passwordEncoder.matches(command.getPassword(), member.getPassword())) {
			log.warn("비밀번호가 일치하지 않습니다");
			throw new InvalidLoginException();
		}

		JwtPayload jwtPayload = new JwtPayload(member.getEmail(), member.getRole().getName(), new Date());
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, jwtService.createToken(jwtPayload));
		cookie.setMaxAge((int)accessKeyExpirationInMs);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);

		response.addCookie(cookie);
	}

}
