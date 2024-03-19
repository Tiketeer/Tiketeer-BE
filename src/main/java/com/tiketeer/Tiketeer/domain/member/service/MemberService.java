package com.tiketeer.Tiketeer.domain.member.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.auth.jwt.JwtPayload;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.domain.member.Otp;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidOtpException;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidTokenException;
import com.tiketeer.Tiketeer.domain.member.exception.MemberIdAndAuthNotMatchedException;
import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.InitMemberPasswordWithOtpCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.RefreshAccessTokenCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.RefreshAccessTokenResultDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.SendPwdChangeEmailCommandDto;
import com.tiketeer.Tiketeer.infra.alarm.email.EmailService;

import io.jsonwebtoken.JwtException;

@Service
@Transactional(readOnly = true)
public class MemberService {
	private final PasswordEncoder passwordEncoder;
	private final OtpRepository otpRepository;
	private final MemberRepository memberRepository;
	private final JwtService jwtService;
	private final EmailService emailService;

	@Autowired
	public MemberService(PasswordEncoder passwordEncoder, OtpRepository otpRepository,
		MemberRepository memberRepository, JwtService jwtService, EmailService emailService) {
		this.passwordEncoder = passwordEncoder;
		this.otpRepository = otpRepository;
		this.memberRepository = memberRepository;
		this.jwtService = jwtService;
		this.emailService = emailService;
	}

	@Transactional
	public void initPasswordWithOtp(InitMemberPasswordWithOtpCommandDto command) {
		var otp = otpRepository.findById(command.getOtp()).orElseThrow(InvalidOtpException::new);

		var member = otp.getMember();
		member.setPassword(passwordEncoder.encode(command.getPassword()));
		member.setEnabled(true);
		otpRepository.delete(otp);
	}

	@Transactional
	public void sendPwdChangeEmail(SendPwdChangeEmailCommandDto command) {
		var memberId = command.getMemberId();
		var email = command.getEmail();

		var member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

		if (!member.getId().equals(memberId)) {
			throw new MemberIdAndAuthNotMatchedException();
		}

		otpRepository.findByMember(member).ifPresent(otpRepository::delete);

		var otp = Otp.builder().member(member).build();
		otpRepository.save(otp);
		// TODO: 훈이 작성 중이신 PR 병합된 이후 작업 재개
		// emailService.sendEmail();
	}

	public RefreshAccessTokenResultDto refreshAccessToken(RefreshAccessTokenCommandDto refreshAccessTokenCommandDto) {
		var refreshToken = refreshAccessTokenCommandDto.getRefreshToken();

		try {
			JwtPayload jwtPayload = jwtService.verifyToken(refreshToken);
			String newAccessToken = jwtService.createToken(
				new JwtPayload(jwtPayload.email(), jwtPayload.roleEnum(), new Date(System.currentTimeMillis())));
			return RefreshAccessTokenResultDto.toDto(newAccessToken);
		} catch (JwtException ex) {
			throw new InvalidTokenException();
		}
	}
}
