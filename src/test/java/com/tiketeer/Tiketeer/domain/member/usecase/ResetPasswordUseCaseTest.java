package com.tiketeer.Tiketeer.domain.member.usecase;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.Otp;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidNewPasswordException;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidOtpException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.ResetPasswordCommandDto;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
class ResetPasswordUseCaseTest {

	@Autowired
	private TestHelper testHelper;
	@Autowired
	private ResetPasswordUseCase resetPasswordUseCase;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private OtpRepository otpRepository;
	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("유저와 OTP 생성 > 비밀번호 변경 > 변경된 비밀번호 일치")
	@Transactional
	void resetPasswordSuccess() {
		//given
		Member member = testHelper.createMember("user@example.com", "password");
		Otp otp = new Otp(LocalDateTime.now().plusDays(1), member);
		otpRepository.save(otp);

		//when
		resetPasswordUseCase.resetPassword(
			new ResetPasswordCommandDto(otp.getPassword(), "newpassword"));

		Member foundMember = memberRepository.findAll().getFirst();
		//then
		assertThat(passwordEncoder.matches("newpassword", foundMember.getPassword())).isTrue();
	}

	@Test
	@DisplayName("유저와 OTP 생성 > 비밀번호 변경 > 만료된 OTP 예외")
	void resetPasswordFailExpiredOtp() {
		//given
		Member member = testHelper.createMember("user@example.com", "password");
		Otp otp = new Otp(LocalDateTime.now().minusDays(1), member);
		otpRepository.save(otp);

		Assertions.assertThatThrownBy(() -> {
			// when
			resetPasswordUseCase.resetPassword(
				new ResetPasswordCommandDto(otp.getPassword(), "newpassword"));
			// then
		}).isInstanceOf(InvalidOtpException.class);
	}

	@Test
	@DisplayName("유저와 OTP 생성 > 비밀번호 변경 > 이전과 동일한 비밀번호 예외")
	void resetPasswordFailSamePassword() {
		//given
		Member member = testHelper.createMember("user@example.com", "password");
		Otp otp = new Otp(LocalDateTime.now().plusDays(1), member);
		otpRepository.save(otp);

		Assertions.assertThatThrownBy(() -> {
			// when
			resetPasswordUseCase.resetPassword(
				new ResetPasswordCommandDto(otp.getPassword(), "password"));
			// then
		}).isInstanceOf(InvalidNewPasswordException.class);
	}
}