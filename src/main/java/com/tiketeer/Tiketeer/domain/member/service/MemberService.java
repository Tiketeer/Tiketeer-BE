package com.tiketeer.Tiketeer.domain.member.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.auth.jwt.JwtPayload;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidOtpException;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidTokenException;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.InitMemberPasswordWithOtpCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.RefreshAccessTokenCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.RefreshAccessTokenResultDto;

import io.jsonwebtoken.JwtException;

@Service
@Transactional(readOnly = true)
public class MemberService {
	private final PasswordEncoder passwordEncoder;
	private final OtpRepository otpRepository;

	private final JwtService jwtService;

	@Autowired
	public MemberService(PasswordEncoder passwordEncoder, OtpRepository otpRepository, JwtService jwtService) {
		this.passwordEncoder = passwordEncoder;
		this.otpRepository = otpRepository;
		this.jwtService = jwtService;
	}

	@Transactional
	public void initPasswordWithOtp(InitMemberPasswordWithOtpCommandDto command) {
		var otp = otpRepository.findById(command.getOtp()).orElseThrow(InvalidOtpException::new);

		var member = otp.getMember();
		member.setPassword(passwordEncoder.encode(command.getPassword()));
		member.setEnabled(true);
		otpRepository.delete(otp);
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
