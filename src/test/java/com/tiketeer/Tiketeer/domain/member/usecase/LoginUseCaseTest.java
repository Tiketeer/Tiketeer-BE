package com.tiketeer.Tiketeer.domain.member.usecase;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.Period;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.tiketeer.Tiketeer.auth.jwt.AccessTokenPayload;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.auth.jwt.RefreshTokenPayload;
import com.tiketeer.Tiketeer.domain.member.RefreshToken;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidLoginException;
import com.tiketeer.Tiketeer.domain.member.repository.RefreshTokenRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.LoginCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.LoginResultDto;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
class LoginUseCaseTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	@Autowired
	private LoginUseCase loginUseCase;
	@Autowired
	private JwtService jwtService;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
		testHelper.createMember("user@example.com", "password");
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("로그인 요청 > 로그인 > 성공")
	void loginSuccess() {

		LoginCommandDto command = LoginCommandDto.builder()
			.email("user@example.com")
			.password("password")
			.build();

		//when
		LoginResultDto loginResult = loginUseCase.login(command);

		//then
		AccessTokenPayload accessTokenPayload = jwtService.createAccessTokenPayload(
			jwtService.verifyToken(loginResult.getAccessToken()));
		assertThat(accessTokenPayload.email()).isEqualTo("user@example.com");
		assertThat(accessTokenPayload.roleEnum()).isEqualTo(RoleEnum.BUYER);

		RefreshTokenPayload refreshTokenPayload = jwtService.createRefreshTokenPayload(
			jwtService.verifyToken(loginResult.getRefreshToken()));

		RefreshToken refreshToken = refreshTokenRepository.findAll().getFirst();

		assertThat(refreshToken).isNotNull();
		assertThat(refreshTokenPayload.tokenId()).isEqualTo(refreshToken.getId().toString());
		assertThat(
			Period.between(LocalDateTime.now().toLocalDate(), refreshToken.getExpiredAt().toLocalDate())
				.getDays()).isEqualTo(
			7);
	}

	@Test
	@DisplayName("DB 내 계정 존재 > 존재하지 않는 이메일로 로그인 요청 > 실패")
	void loginFailInvalidEmail() {
		LoginCommandDto command = LoginCommandDto.builder()
			.email("nobody@example.com")
			.password("password")
			.build();

		//when - then
		Assertions.assertThrows(InvalidLoginException.class, () -> {
			loginUseCase.login(command);
		});
	}

	@Test
	@DisplayName("DB 내 계정 존재 > 잘못된 비밀번호로 로그인 요청 > 실패")
	void loginFailInvalidPassword() {

		LoginCommandDto command = LoginCommandDto.builder()
			.email("user@example.com")
			.password("invalid")
			.build();

		//when - then
		Assertions.assertThrows(InvalidLoginException.class, () -> {
			loginUseCase.login(command);
		});

	}

}