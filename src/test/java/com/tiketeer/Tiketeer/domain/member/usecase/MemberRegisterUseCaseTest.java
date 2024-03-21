package com.tiketeer.Tiketeer.domain.member.usecase;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

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
import com.tiketeer.Tiketeer.domain.member.controller.dto.MemberRegisterRequestDto;
import com.tiketeer.Tiketeer.domain.member.exception.DuplicatedEmailException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.MemberRegisterCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.MemberRegisterResultDto;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.exception.RoleNotFoundException;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Import({TestHelper.class})
@SpringBootTest
@DisplayName("Member Register Test")
class MemberRegisterUseCaseTest {
	@Autowired
	private MemberRegisterUseCase memberRegisterUseCase;

	@Autowired
	private TestHelper testHelper;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private OtpRepository otpRepository;

	@PersistenceContext
	private EntityManager em;

	private Member buyer;
	private Role buyerRole;

	private Otp otp;

	@BeforeEach
	void init() {
		testHelper.initDB();

		Optional<Role> role = roleRepository.findByName(RoleEnum.BUYER);
		buyerRole = role.get();
		buyer = memberRepository.save(new Member("test@gmail.com", "asdf1234", 0, false, null, buyerRole));

		Otp otp1 = new Otp(LocalDateTime.of(2025, 3, 15, 15, 15, 15), buyer);
		otp = otpRepository.save(otp1);
	}

	@AfterEach
	void clean() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("회원정보 > 회원가입 > 저장된 값과 회원정보 비교")
	void registerSuccess() {
		// given
		MemberRegisterRequestDto memberDto = new MemberRegisterRequestDto("test22@gmail.com", false);

		// when
		MemberRegisterResultDto registerMemberResultDto = memberRegisterUseCase.register(
			MemberRegisterCommandDto.builder().email(memberDto.getEmail()).isSeller(memberDto.getIsSeller()).build());

		Optional<Member> optionalMember = memberRepository.findByEmail("test22@gmail.com");
		Otp otp = otpRepository.findByMember(optionalMember.get()).orElseThrow();

		// then
		assertThat(optionalMember.get().getId()).isEqualTo(registerMemberResultDto.getMemberId());
		assertThat(otp.getMember().getId()).isEqualTo(optionalMember.get().getId());
	}

	@Test
	@DisplayName("회원정보 > 회원가입 > 이미 가입되어 있지만 비활성화 상태")
	void registerSuccessAlreadyRegistered() {
		// given
		MemberRegisterRequestDto memberDto = new MemberRegisterRequestDto("test@gmail.com", false);

		// when
		memberRegisterUseCase.register(
			MemberRegisterCommandDto.builder().email(memberDto.getEmail()).isSeller(memberDto.getIsSeller()).build());

		// then
		Optional<Member> optionalMember = memberRepository.findByEmail("test@gmail.com");
		assertThat(optionalMember.get().getId()).isEqualTo(buyer.getId());

		Otp otp2 = otpRepository.findByMember(optionalMember.get()).orElseThrow();
		assertThat(otp2.getPassword()).isNotEqualTo(otp.getPassword());
	}

	@Test
	@DisplayName("회원정보 > 회원가입 > 정의된 롤이 없음")
	void registerFailNotFoundRule() {
		// given
		testHelper.cleanDB();
		MemberRegisterRequestDto memberDto = new MemberRegisterRequestDto("test@gmail.com", false);

		// when
		// then
		Assertions.assertThatThrownBy(() -> memberRegisterUseCase.register(
				MemberRegisterCommandDto.builder().email(memberDto.getEmail()).isSeller(memberDto.getIsSeller()).build()))
			.isInstanceOf(
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

		MemberRegisterRequestDto memberDto = new MemberRegisterRequestDto("test@gmail.com", false);

		// when
		// then
		Assertions.assertThatThrownBy(() -> memberRegisterUseCase.register(
				MemberRegisterCommandDto.builder().email(memberDto.getEmail()).isSeller(memberDto.getIsSeller()).build()))
			.isInstanceOf(
				DuplicatedEmailException.class);
	}
}