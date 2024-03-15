package com.tiketeer.Tiketeer.domain.member.application;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.dto.MemberDto.RegisterMemberDto;
import com.tiketeer.Tiketeer.domain.member.dto.MemberDto.RegisterMemberResponseDto;
import com.tiketeer.Tiketeer.domain.member.exception.DuplicatedEmailException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.exception.RoleNotFoundException;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;

@Service
public class MemberRegisterService {
	private final MemberRepository memberRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public MemberRegisterService(MemberRepository memberRepository, RoleRepository roleRepository,
		PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public RegisterMemberResponseDto register(RegisterMemberDto registerMemberDto) {
		Optional<Member> optionalMember = memberRepository.findByEmail(registerMemberDto.getEmail());
		if (optionalMember.isPresent()) {
			if (optionalMember.get().isEnabled()) {
				throw new DuplicatedEmailException();
			}

			return RegisterMemberResponseDto.toDto(optionalMember.get());
		}

		Role role = roleRepository.findByName(registerMemberDto.getIsSeller() ? RoleEnum.SELLER : RoleEnum.BUYER)
			.orElseThrow(RoleNotFoundException::new);
		String encodedPassword = passwordEncoder.encode(registerMemberDto.getPassword());

		Member member = Member.builder()
			.email(registerMemberDto.getEmail())
			.password(encodedPassword)
			.point(0L)
			.enabled(false)
			.profileUrl(null)
			.role(role)
			.build();
		Member saved = memberRepository.save(member);
		return RegisterMemberResponseDto.toDto(saved);
	}
}
