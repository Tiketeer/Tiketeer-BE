package com.tiketeer.Tiketeer.domain.member.application;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.Otp;
import com.tiketeer.Tiketeer.domain.member.dto.MemberDto.RegisterMemberDto;
import com.tiketeer.Tiketeer.domain.member.dto.MemberDto.RegisterMemberResponseDto;
import com.tiketeer.Tiketeer.domain.member.exception.DuplicatedEmailException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.exception.RoleNotFoundException;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;

@Service
@Transactional(readOnly = true)
public class MemberRegisterService {
	private final MemberRepository memberRepository;
	private final RoleRepository roleRepository;
	private final OtpRepository otpRepository;
	private final PasswordEncoder passwordEncoder;

	private final int OTP_VALID_TIME = 30;

	@Autowired
	public MemberRegisterService(MemberRepository memberRepository, RoleRepository roleRepository,
		OtpRepository otpRepository,
		PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.roleRepository = roleRepository;
		this.otpRepository = otpRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
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

		Otp otp = Otp.builder().member(saved).expiredAt(LocalDateTime.now().plusMinutes(OTP_VALID_TIME)).build();
		otpRepository.save(otp);

		return RegisterMemberResponseDto.toDto(saved);
	}
}
