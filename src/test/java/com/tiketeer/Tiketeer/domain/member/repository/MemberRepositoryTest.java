package com.tiketeer.Tiketeer.domain.member.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;

@DataJpaTest
class MemberRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Test
	@DisplayName("멤버 저장 > 멤버 조회 > 저장한 값과 조회된 값 비교")
	void findByEmail() {
		Role role = roleRepository.save(new Role(RoleEnum.BUYER));
		Member saved = memberRepository.save(
			new Member("test@gmail.com", "asdf1234", 0L, false, null, role));

		Optional<Member> optionalMember = memberRepository.findByEmail(saved.getEmail());

		assertThat(optionalMember.get().getId()).isEqualTo(saved.getId());
	}
}