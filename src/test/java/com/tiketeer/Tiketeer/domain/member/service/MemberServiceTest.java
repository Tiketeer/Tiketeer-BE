package com.tiketeer.Tiketeer.domain.member.service;

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
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.Otp;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidOtpException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.AuthMemberWithEmailOtpCommandDto;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class MemberServiceTest {
	private final TestHelper testHelper;
	private final MemberService memberService;
	private final RoleRepository roleRepository;
	private final MemberRepository memberRepository;
	private final OtpRepository otpRepository;

	@Autowired
	public MemberServiceTest(TestHelper testHelper, MemberService memberService, RoleRepository roleRepository,
		MemberRepository memberRepository,
		OtpRepository otpRepository) {
		this.testHelper = testHelper;
		this.memberService = memberService;
		this.roleRepository = roleRepository;
		this.memberRepository = memberRepository;
		this.otpRepository = otpRepository;
	}

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("유효하지 않은 OTP > MemberService.authMemberWithEmailOtp 호출 > 실패")
	@Transactional
	void authMemberWithEmailOtpFailBecauseInvalidOtp() {
		// given
		var mockEmail = "test@test.com";
		var member = createMember(mockEmail);

		otpRepository.save(
			Otp.builder()
				.member(member)
				.expiredAt(LocalDateTime.of(9999, 12, 31, 0, 0))
				.build());

		var invalidOtp = UUID.randomUUID();

		Assertions.assertThatThrownBy(() -> {
			// when
			memberService.authMemberWithEmailOtp(AuthMemberWithEmailOtpCommandDto.builder().otp(invalidOtp).build());
			// then
		}).isInstanceOf(InvalidOtpException.class);
	}

	@Test
	@DisplayName("유효한 OTP > MemberService.authMemberWithEmailOtp 호출 > 성공")
	@Transactional
	void authMemberWithEmailOtpSuccess() {
		// given
		var mockEmail = "test@test.com";
		var member = createMember(mockEmail);

		var otp = otpRepository.save(
			Otp.builder()
				.member(member)
				.expiredAt(LocalDateTime.of(9999, 12, 31, 0, 0))
				.build());

		// when
		memberService.authMemberWithEmailOtp(
			AuthMemberWithEmailOtpCommandDto.builder().otp(otp.getPassword()).build());

		// then
		var memberAfterEmailAuthOtp = memberRepository.findByEmail(mockEmail);
		Assertions.assertThat(memberAfterEmailAuthOtp.isPresent()).isTrue();

		var memberAfterEmailAuth = memberAfterEmailAuthOtp.get();
		Assertions.assertThat(memberAfterEmailAuth.isEnabled()).isTrue();

		Assertions.assertThat(otpRepository.findById(otp.getPassword()).isPresent()).isFalse();
	}

	private Member createMember(String email) {
		var role = roleRepository.findByName(RoleEnum.BUYER).orElseThrow();
		var memberForSave = Member.builder()
			.email(email)
			.password("1234456eqeqw").role(role).build();
		return memberRepository.save(memberForSave);
	}
}
