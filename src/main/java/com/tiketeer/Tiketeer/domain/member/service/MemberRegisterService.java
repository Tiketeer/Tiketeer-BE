package com.tiketeer.Tiketeer.domain.member.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.Otp;
import com.tiketeer.Tiketeer.domain.member.exception.DuplicatedEmailException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.CreateEmailViewCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.MemberRegisterCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.MemberRegisterResultDto;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.exception.RoleNotFoundException;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.infra.alarm.email.EmailService;
import com.tiketeer.Tiketeer.infra.alarm.email.view.EmailViewFactory;

@Service
@Transactional(readOnly = true)
public class MemberRegisterService {
	private final MemberRepository memberRepository;
	private final RoleRepository roleRepository;
	private final OtpRepository otpRepository;
	private final EmailService emailService;
	private final EmailViewFactory emailViewFactory;
	private final int OTP_VALID_TIME = 30;
	private final String AUTHENTICATE_EMAIL_TITLE = "[tiketeer] 인정메일 발송";

	@Autowired
	public MemberRegisterService(MemberRepository memberRepository, RoleRepository roleRepository,
		OtpRepository otpRepository, EmailService emailService, EmailViewFactory emailViewFactory) {
		this.memberRepository = memberRepository;
		this.roleRepository = roleRepository;
		this.otpRepository = otpRepository;
		this.emailService = emailService;
		this.emailViewFactory = emailViewFactory;
	}

	@Transactional
	public MemberRegisterResultDto register(MemberRegisterCommandDto registerMemberDto) {
		Optional<Member> optionalMember = memberRepository.findByEmail(registerMemberDto.getEmail());
		if (optionalMember.isPresent()) {
			var member = optionalMember.get();
			if (member.isEnabled()) {
				throw new DuplicatedEmailException();
			}

			otpRepository.findByMember(member).ifPresent(otp -> {
				otpRepository.delete(otp);
				otpRepository.flush();
			});

			Otp otp =
				Otp.builder()
					.member(member)
					.expiredAt(LocalDateTime.now().plusMinutes(OTP_VALID_TIME))
					.build();
			otpRepository.save(otp);

			// TODO: async task
			var viewData = CreateEmailViewCommandDto.builder().email(member.getEmail()).otp(otp.getPassword()).build();
			emailService.sendEmail(member.getEmail(), AUTHENTICATE_EMAIL_TITLE, emailViewFactory.createView(viewData));
			return MemberRegisterResultDto.toDto(optionalMember.get());
		}

		Role role = roleRepository.findByName(registerMemberDto.getIsSeller() ? RoleEnum.SELLER : RoleEnum.BUYER)
			.orElseThrow(RoleNotFoundException::new);

		Member member = Member.builder()
			.email(registerMemberDto.getEmail())
			.point(0L)
			.enabled(false)
			.profileUrl(null)
			.role(role)
			.build();
		Member saved = memberRepository.save(member);

		Otp otp = Otp.builder().member(saved).expiredAt(LocalDateTime.now().plusMinutes(OTP_VALID_TIME)).build();
		otpRepository.save(otp);

		// TODO: async task
		var viewData = CreateEmailViewCommandDto.builder().email(saved.getEmail()).otp(otp.getPassword()).build();
		emailService.sendEmail(saved.getEmail(), AUTHENTICATE_EMAIL_TITLE, emailViewFactory.createView(viewData));
		return MemberRegisterResultDto.toDto(saved);
	}
}
