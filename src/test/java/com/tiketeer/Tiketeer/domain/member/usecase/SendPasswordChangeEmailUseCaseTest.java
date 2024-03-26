package com.tiketeer.Tiketeer.domain.member.usecase;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Otp;
import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.SendPwdChangeEmailCommandDto;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class SendPasswordChangeEmailUseCaseTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private SendPasswordChangeEmailUseCase sendPasswordChangeEmailUseCase;
	@Autowired
	private OtpRepository otpRepository;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("존재하지 않은 멤버 이메일 > 비밀번호 변경 메일 전송 요청 > 실패")
	void sendEmailFailBecauseMemberNotFound() {
		// given
		var invalidEmail = "test@test.com";
		var command = SendPwdChangeEmailCommandDto.builder()
			.email(invalidEmail)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			sendPasswordChangeEmailUseCase.sendEmail(command);
			// then
		}).isInstanceOf(MemberNotFoundException.class);
	}

	@Test
	@DisplayName("이미 멤버 이하에 Otp가 존재하는 상황 > 비밀번호 변경 메일 전송 요청 > 성공 및 기존 Otp 삭제")
	@Transactional
	void sendEmailSuccessWithAlreadyOtpExist() {
		// given
		var now = LocalDateTime.now();
		var email = "test@test.com";
		var member = testHelper.createMember(email);
		var otpAlreadyExist = otpRepository.save(Otp.builder().member(member).expiredAt(now.plusMinutes(30)).build());
		Assertions.assertThat(otpRepository.findById(otpAlreadyExist.getPassword()).isPresent()).isTrue();
		var command = SendPwdChangeEmailCommandDto.builder()
			.email(email)
			.build();

		// when
		sendPasswordChangeEmailUseCase.sendEmail(command);

		// then
		var newOtpOpt = otpRepository.findByMember(member);
		Assertions.assertThat(newOtpOpt.isPresent()).isTrue();

		var newOtp = newOtpOpt.get();
		Assertions.assertThat(newOtp.getPassword()).isNotEqualTo(otpAlreadyExist.getPassword());
	}

	@Test
	@DisplayName("정상 컨디션 > 비밀번호 변경 메일 전송 요청 > 성공 및 기존 Otp 삭제")
	@Transactional
	void sendEmailSuccess() {
		// given
		var now = LocalDateTime.now();
		var email = "test@test.com";
		var member = testHelper.createMember(email);

		var command = SendPwdChangeEmailCommandDto.builder()
			.email(email)
			.commandCreatedAt(now)
			.build();

		// when
		sendPasswordChangeEmailUseCase.sendEmail(command);

		// then
		var newOtpOpt = otpRepository.findByMember(member);
		Assertions.assertThat(newOtpOpt.isPresent()).isTrue();

		var newOtp = newOtpOpt.get();
		Assertions.assertThat(newOtp.getExpiredAt()).isEqualToIgnoringNanos(now.plusMinutes(Otp.OTP_VALID_MINUTE));
	}
}
