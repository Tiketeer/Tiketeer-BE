package com.tiketeer.Tiketeer.domain.member.usecase;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.auth.jwt.AccessTokenPayload;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.auth.jwt.RefreshTokenPayload;
import com.tiketeer.Tiketeer.domain.member.RefreshToken;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidTokenException;
import com.tiketeer.Tiketeer.domain.member.repository.RefreshTokenRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.RefreshAccessTokenCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.RefreshAccessTokenResultDto;

import io.jsonwebtoken.JwtException;

@Service
public class RefreshAccessTokenUseCase {
	private final JwtService jwtService;
	private final RefreshTokenRepository refreshTokenRepository;

	public RefreshAccessTokenUseCase(JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
		this.jwtService = jwtService;
		this.refreshTokenRepository = refreshTokenRepository;
	}

	@Transactional(readOnly = true)
	public RefreshAccessTokenResultDto refresh(RefreshAccessTokenCommandDto refreshAccessTokenCommandDto) {
		var refreshToken = refreshAccessTokenCommandDto.getRefreshToken();

		try {
			RefreshTokenPayload refreshTokenPayload = jwtService.createRefreshTokenPayload(
				jwtService.verifyToken(refreshToken));

			RefreshToken refreshTokenFromDB =
				refreshTokenRepository.findById(UUID.fromString(refreshTokenPayload.tokenId()))
					.orElseThrow(InvalidTokenException::new);

			String newAccessToken = jwtService.createAccessToken(
				new AccessTokenPayload(refreshTokenFromDB.getMember().getEmail(),
					refreshTokenFromDB.getMember().getRole().getName(),
					new Date(System.currentTimeMillis())));
			return RefreshAccessTokenResultDto.toDto(newAccessToken);
		} catch (JwtException ex) {
			throw new InvalidTokenException();
		}
	}
}
