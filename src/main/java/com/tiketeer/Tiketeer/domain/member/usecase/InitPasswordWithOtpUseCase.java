package com.tiketeer.Tiketeer.domain.member.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.InvalidOtpException;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.InitMemberPasswordWithOtpCommandDto;

@Service
public class InitPasswordWithOtpUseCase {
	private final OtpRepository otpRepository;
	private final PasswordEncoder passwordEncoder;

	public InitPasswordWithOtpUseCase(OtpRepository otpRepository, PasswordEncoder passwordEncoder) {
		this.otpRepository = otpRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public void init(InitMemberPasswordWithOtpCommandDto command) {
		var otp = otpRepository.findById(command.getOtp()).orElseThrow(InvalidOtpException::new);

		var member = otp.getMember();
		member.setPassword(passwordEncoder.encode(command.getPassword()));
		member.setEnabled(true);
		otpRepository.delete(otp);
	}
}
