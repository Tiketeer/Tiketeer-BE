package com.tiketeer.Tiketeer.domain.member.usecase;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.tiketeer.Tiketeer.auth.jwt.JwtPayload;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidTokenException;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.RefreshAccessTokenCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.RefreshAccessTokenResultDto;

import io.jsonwebtoken.JwtException;

@Service
public class RefreshAccessTokenUseCase {
	private final JwtService jwtService;

	public RefreshAccessTokenUseCase(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	public RefreshAccessTokenResultDto refresh(RefreshAccessTokenCommandDto refreshAccessTokenCommandDto) {
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
