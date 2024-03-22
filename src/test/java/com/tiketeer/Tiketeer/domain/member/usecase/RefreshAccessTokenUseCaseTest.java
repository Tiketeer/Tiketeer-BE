package com.tiketeer.Tiketeer.domain.member.usecase;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.tiketeer.Tiketeer.auth.jwt.AccessTokenPayload;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.auth.jwt.RefreshTokenPayload;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.RefreshToken;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidTokenException;
import com.tiketeer.Tiketeer.domain.member.repository.RefreshTokenRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.LoginCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.LoginResultDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.RefreshAccessTokenCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.RefreshAccessTokenResultDto;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
class RefreshAccessTokenUseCaseTest {
	@Autowired
	private TestHelper testHelper;

	@Autowired
	private RefreshAccessTokenUseCase refreshAccessTokenUseCase;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private LoginUseCase loginUseCase;

	@BeforeEach
	void init() {
		testHelper.initDB();
	}

	@AfterEach
	void clear() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("정상 토큰 > 재발급 > 재발급 확인")
	void refreshAccessTokenSuccess() {
		// given
		LoginResultDto loginResultDto = testHelper.registerAndLoginAndReturnAccessTokenAndRefreshToken("test@gmail.com",
			RoleEnum.BUYER);
		String refreshToken = loginResultDto.getRefreshToken();

		// when
		RefreshAccessTokenResultDto refreshAccessTokenResultDto = refreshAccessTokenUseCase.refresh(
			RefreshAccessTokenCommandDto.builder().refreshToken(refreshToken).build());

		// then
		String accessToken1 = refreshAccessTokenResultDto.getAccessToken();
		AccessTokenPayload accessTokenPayload = jwtService.createAccessTokenPayload(
			jwtService.verifyToken(accessToken1));

		Assertions.assertThat(accessToken1).isNotNull();
		Assertions.assertThat(accessTokenPayload.email()).isEqualTo("test@gmail.com");
	}

	@Test
	@DisplayName("refreshToken 만료 > 재발급 > 예외발생 확인")
	void refreshAccessTokenFailByExpiredRefreshToken() {
		// given
		Member member = testHelper.createMember("test@gmail.com");
		RefreshToken saved = refreshTokenRepository.save(
			RefreshToken.builder().member(member).expiredAt(LocalDateTime.now().minusDays(1)).build());
		String refreshToken = jwtService.createRefreshToken(
			new RefreshTokenPayload(saved.getId().toString(),
				new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)));

		// when
		// then
		assertThrows(InvalidTokenException.class, () -> {
			refreshAccessTokenUseCase.refresh(
				RefreshAccessTokenCommandDto.builder().refreshToken(refreshToken).build());
		});
	}

	@Test
	@DisplayName("중복 로그인(기존 refresh token 삭제) > 삭제된 refresh token으로 refresh 시도 > 예외 발생")
	void refreshAccessTokenFailByDuplicatedLogin() {
		// given
		String email = "test@gmail.com";
		LoginResultDto loginResultDto = testHelper.registerAndLoginAndReturnAccessTokenAndRefreshToken(email,
			RoleEnum.BUYER);

		loginUseCase.login(LoginCommandDto.builder().email(email).password("1q2w3e4r!!").build());

		String refreshToken = loginResultDto.getRefreshToken();

		// then
		assertThrows(InvalidTokenException.class, () -> {
			// when
			refreshAccessTokenUseCase.refresh(
				RefreshAccessTokenCommandDto.builder().refreshToken(refreshToken).build());
		});
	}
}