package com.tiketeer.Tiketeer.domain.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.InvalidOtpException;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.InitMemberPasswordWithOtpCommandDto;

@Service
@Transactional(readOnly = true)
public class MemberService {
	private final PasswordEncoder passwordEncoder;
	private final OtpRepository otpRepository;

	@Autowired
	public MemberService(PasswordEncoder passwordEncoder, OtpRepository otpRepository) {
		this.passwordEncoder = passwordEncoder;
		this.otpRepository = otpRepository;
	}

	@Transactional
	public void initPasswordWithOtp(InitMemberPasswordWithOtpCommandDto command) {
		var otp = otpRepository.findById(command.getOtp()).orElseThrow(InvalidOtpException::new);

		var member = otp.getMember();
		member.setPassword(passwordEncoder.encode(command.getPassword()));
		member.setEnabled(true);
		otpRepository.delete(otp);
	}
}
