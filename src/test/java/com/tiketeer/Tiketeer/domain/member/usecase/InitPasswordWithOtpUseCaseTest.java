package com.tiketeer.Tiketeer.domain.member.usecase;

import java.time.LocalDateTime;
import java.util.UUID;

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

import com.tiketeer.Tiketeer.domain.member.Otp;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidOtpException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.InitMemberPasswordWithOtpCommandDto;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
class InitPasswordWithOtpUseCaseTest {
	@Autowired
	private TestHelper testHelper;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private OtpRepository otpRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private InitPasswordWithOtpUseCase initPasswordWithOtpUseCase;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("유효하지 않은 OTP > InitPasswordWithOtpUseCase.init 호출 > 실패")
	@Transactional
	void initPasswordWithOtpFailBecauseInvalidOtp() {
		// given
		var mockEmail = "test@test.com";
		var member = testHelper.createMember(mockEmail);

		otpRepository.save(
			Otp.builder()
				.member(member)
				.expiredAt(LocalDateTime.of(9999, 12, 31, 0, 0))
				.build());

		var invalidOtp = UUID.randomUUID();

		Assertions.assertThatThrownBy(() -> {
			// when
			initPasswordWithOtpUseCase.init(InitMemberPasswordWithOtpCommandDto.builder().otp(invalidOtp).build());
			// then
		}).isInstanceOf(InvalidOtpException.class);
	}

	@Test
	@DisplayName("유효한 OTP > InitPasswordWithOtpUseCase.init 호출 > 성공")
	@Transactional
	void initPasswordWithOtpSuccess() {
		// given
		var mockEmail = "test@test.com";
		var member = testHelper.createMember(mockEmail);

		var otp = otpRepository.save(
			Otp.builder()
				.member(member)
				.expiredAt(LocalDateTime.of(9999, 12, 31, 0, 0))
				.build());

		var mockPwd = "1qwertasdcxz";

		// when
		initPasswordWithOtpUseCase.init(
			InitMemberPasswordWithOtpCommandDto.builder().otp(otp.getPassword()).password(mockPwd).build());

		// then
		var memberAfterEmailAuthOpt = memberRepository.findByEmail(mockEmail);
		Assertions.assertThat(memberAfterEmailAuthOpt.isPresent()).isTrue();

		var memberAfterEmailAuth = memberAfterEmailAuthOpt.get();
		Assertions.assertThat(memberAfterEmailAuth.isEnabled()).isTrue();
		Assertions.assertThat(passwordEncoder.matches(mockPwd, memberAfterEmailAuth.getPassword())).isTrue();

		Assertions.assertThat(otpRepository.findById(otp.getPassword()).isPresent()).isFalse();
	}
}