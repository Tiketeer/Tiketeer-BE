package com.tiketeer.Tiketeer.domain.member.usecase;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.Otp;
import com.tiketeer.Tiketeer.domain.member.exception.MemberIdAndAuthNotMatchedException;
import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.SendPwdChangeEmailCommandDto;
import com.tiketeer.Tiketeer.infra.alarm.email.EmailService;
import com.tiketeer.Tiketeer.infra.alarm.email.view.AuthenticateEmailViewStrategy;

@Service
public class SendPasswordChangeEmailUseCase {
	private final MemberRepository memberRepository;
	private final OtpRepository otpRepository;
	private final EmailService emailService;
	private final String baseUrl;
	private final String port;
	private static final String PASSWORD_CHANGE_EMAIL_TITLE = "[tiketeer] 비밀번호 변경";

	@Autowired
	public SendPasswordChangeEmailUseCase(
		MemberRepository memberRepository,
		OtpRepository otpRepository,
		EmailService emailService,
		@Value("${custom.service.baseUrl}") String baseUrl,
		@Value("${server.port}") String port
	) {
		this.memberRepository = memberRepository;
		this.otpRepository = otpRepository;
		this.emailService = emailService;
		this.baseUrl = baseUrl;
		this.port = port;
	}

	@Transactional
	public void sendEmail(SendPwdChangeEmailCommandDto command) {
		var member = memberRepository.findById(command.getMemberId()).orElseThrow(MemberNotFoundException::new);

		if (!member.getEmail().equals(command.getEmail())) {
			throw new MemberIdAndAuthNotMatchedException();
		}

		deleteOtpWithMember(member);

		var now = command.getCommandCreatedAt();

		var otp = otpRepository.save(
			Otp.builder()
				.member(member)
				.expiredAt(now.plusMinutes(Otp.OTP_VALID_MINUTE))
				.build());

		// TODO: async task
		sendEmail(member.getEmail(), otp.getPassword());
	}

	private void deleteOtpWithMember(Member member) {
		otpRepository.findByMember(member).ifPresent(otp -> {
			otpRepository.delete(otp);
			otpRepository.flush();
		});
	}

	private void sendEmail(String email, UUID otp) {
		var emailViewStrategy = AuthenticateEmailViewStrategy.builder()
			.email(email)
			.otp(otp)
			.baseUrl(baseUrl)
			.port(port)
			.build();
		emailService.sendEmail(email, PASSWORD_CHANGE_EMAIL_TITLE, emailViewStrategy);
	}
}
