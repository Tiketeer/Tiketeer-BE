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
import com.tiketeer.Tiketeer.domain.member.usecase.dto.LoginCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.LoginResultDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class LoginUseCase {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public LoginUseCase(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public LoginResultDto login(LoginCommandDto command) {

		Member member = getValidatedMember(command.getEmail(), command.getPassword());
		String accessToken = generateToken(member);

		return new LoginResultDto(accessToken);
	}

	private String generateToken(Member member) {
		JwtPayload jwtPayload = new JwtPayload(member.getEmail(), member.getRole().getName(), new Date());
		return jwtService.createToken(jwtPayload);
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
