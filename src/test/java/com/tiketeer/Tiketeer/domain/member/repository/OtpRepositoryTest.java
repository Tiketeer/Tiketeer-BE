package com.tiketeer.Tiketeer.domain.member.repository;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.Otp;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;

@DataJpaTest
class OtpRepositoryTest {
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private OtpRepository otpRepository;

	@Test
	@DisplayName("멤버, OTP 저장 > 멤버로 조회 > 조회된 멤버와 저장한 멤버 비교")
	void findByMemberSuccess() {
		Role role = roleRepository.save(new Role(RoleEnum.BUYER));
		Member buyer = memberRepository.save(new Member("test@gmail.com", null, 0, false, null, role));

		Otp otp1 = new Otp(LocalDateTime.of(2025, 3, 15, 15, 15, 15), buyer);
		otpRepository.save(otp1);

		Otp otp = otpRepository.findByMember(buyer).orElseThrow();

		Assertions.assertThat(otp.getPassword()).isEqualTo(otp1.getPassword());
	}
}