package com.tiketeer.Tiketeer.domain.member.application;

import static com.tiketeer.Tiketeer.domain.member.dto.MemberDto.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.exception.DuplicatedEmailException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.exception.RoleNotFoundException;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@DisplayName("Member Register Test")
class MemberRegisterServiceTest {
	@Autowired
	private MemberRegisterService memberRegisterService;

	@Autowired
	private TestHelper testHelper;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private MemberRepository memberRepository;

	@PersistenceContext
	private EntityManager em;

	private Member buyer;
	private Role buyerRole;

	@BeforeEach
	void init() {
		testHelper.initDB();

		Optional<Role> role = roleRepository.findByName(RoleEnum.BUYER);
		buyerRole = role.get();
		buyer = memberRepository.save(new Member("test@gmail.com", "asdf1234", 0, false, null, buyerRole));
	}

	@AfterEach
	void clean() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("회원정보 > 회원가입 > 저장된 값과 회원정보 비교")
	void registerSuccess() {
		// given
		RegisterMemberDto memberDto = new RegisterMemberDto("test22@gmail.com", "asdf1234", false);

		// when
		RegisterMemberResponseDto registerMemberResponseDto = memberRegisterService.register(memberDto);

		Optional<Member> optionalMember = memberRepository.findByEmail("test@gmail.com");

		// then
		assertThat(optionalMember.get().getId()).isEqualTo(registerMemberResponseDto.getMemberId());
	}

	@Test
	@DisplayName("회원정보 > 회원가입 > 이미 가입되어 있지만 비활성화 상태")
	void registerSuccessAlreadyRegistered() {
		// given
		RegisterMemberDto memberDto = new RegisterMemberDto("test@gmail.com", "asdf1234", false);

		// when
		RegisterMemberResponseDto registerMemberResponseDto = memberRegisterService.register(memberDto);

		Optional<Member> optionalMember = memberRepository.findByEmail("test@gmail.com");

		// then
		assertThat(optionalMember.get().getId()).isEqualTo(buyer.getId());
	}

	@Test
	@DisplayName("회원정보 > 회원가입 > 정의된 롤이 없음")
	void registerFailNotFoundRule() {
		// given
		testHelper.cleanDB();
		RegisterMemberDto memberDto = new RegisterMemberDto("test@gmail.com", "asdf1234", false);

		// when
		// then
		Assertions.assertThatThrownBy(() -> memberRegisterService.register(memberDto)).isInstanceOf(
			RoleNotFoundException.class);
	}

	@Test
	@DisplayName("회원정보 > 회원가입 > 중복된 이메일 실패")
	@Transactional
	void registerFailDuplicatedEmail() {
		// given
		buyer.setEnabled(true);
		em.flush();
		em.clear();

		RegisterMemberDto memberDto = new RegisterMemberDto("test@gmail.com", "asdf1234", false);

		// when
		// then
		Assertions.assertThatThrownBy(() -> memberRegisterService.register(memberDto)).isInstanceOf(
			DuplicatedEmailException.class);
	}
}