package com.tiketeer.Tiketeer.domain.member.usecase;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.tiketeer.Tiketeer.auth.jwt.JwtPayload;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidTokenException;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.RefreshAccessTokenCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.RefreshAccessTokenResultDto;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
class RefreshAccessTokenUseCaseTest {
	@Autowired
	private RefreshAccessTokenUseCase refreshAccessTokenUseCase;

	@Autowired
	private JwtService jwtService;

	@Test
	@DisplayName("정상 토큰 > 재발급 > 재발급 확인")
	void refreshAccessToken() {
		// given
		String refreshToken = jwtService.createToken(
			new JwtPayload("test@gmail.com", RoleEnum.BUYER, new Date(System.currentTimeMillis())));

		// when
		RefreshAccessTokenResultDto refreshAccessTokenResultDto = refreshAccessTokenUseCase.refresh(
			RefreshAccessTokenCommandDto.builder().refreshToken(refreshToken).build());

		// then
		String accessToken1 = refreshAccessTokenResultDto.getAccessToken();
		JwtPayload jwtPayload = jwtService.verifyToken(accessToken1);

		Assertions.assertThat(accessToken1).isNotNull();
		Assertions.assertThat(jwtPayload.email()).isEqualTo("test@gmail.com");
	}

	@Test
	@DisplayName("refreshToken 만료 > 재발급 > 예외발생 확인")
	void refreshAccessTokenFailByExpiredRefreshToken() {
		// given
		String refreshToken = jwtService.createToken(
			new JwtPayload("test@gmail.com", RoleEnum.BUYER,
				new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)));

		// when
		// then
		assertThrows(InvalidTokenException.class, () -> {
			refreshAccessTokenUseCase.refresh(
				RefreshAccessTokenCommandDto.builder().refreshToken(refreshToken).build());
		});
	}
}