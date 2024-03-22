package com.tiketeer.Tiketeer.domain.member.repository;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.RefreshToken;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;

@DataJpaTest
class RefreshTokenRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Test
	@DisplayName("refresh token 저장 > member로 refresh token 조회 > 조회한 refresh token 검증")
	void findByMemberSuccess() {
		Role role = roleRepository.save(new Role(RoleEnum.BUYER));
		Member member = memberRepository.save(Member.builder()
			.email("test@mail.com")
			.role(role)
			.password("1q2w3e4r!!")
			.point(0L)
			.enabled(true)
			.profileUrl(null)
			.build());

		RefreshToken saved = refreshTokenRepository.save(
			RefreshToken.builder().member(member).expiredAt(LocalDateTime.now().plusDays(7)).build());

		RefreshToken refreshToken = refreshTokenRepository.findByMember(member).orElseThrow();

		Assertions.assertThat(refreshToken.getId()).isEqualTo(saved.getId());
	}
}