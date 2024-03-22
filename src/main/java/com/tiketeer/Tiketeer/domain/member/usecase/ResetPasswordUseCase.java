package com.tiketeer.Tiketeer.domain.member.usecase;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Otp;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidNewPasswordException;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidOtpException;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.ResetPasswordCommandDto;

@Service
public class ResetPasswordUseCase {

	private final OtpRepository otpRepository;
	private final PasswordEncoder passwordEncoder;

	public ResetPasswordUseCase(OtpRepository otpRepository, PasswordEncoder passwordEncoder) {
		this.otpRepository = otpRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public void resetPassword(ResetPasswordCommandDto command) {
		Otp otpEntity = validateOtpAndReturnEntity(command.getOtp());
		validateIfSamePassword(command.getNewPassword(), otpEntity.getMember().getPassword());
		otpEntity.getMember().setPassword(passwordEncoder.encode(command.getNewPassword()));
	}

	private void validateIfSamePassword(String newPassword, String oldPasswordHash) {
		if (passwordEncoder.matches(newPassword, oldPasswordHash)) {
			throw new InvalidNewPasswordException();
		}
	}

	private Otp validateOtpAndReturnEntity(UUID otp) {
		Otp otpEntity = otpRepository.findById(otp).orElseThrow(InvalidOtpException::new);

		if (LocalDateTime.now().isAfter(otpEntity.getExpiredAt())) {
			throw new InvalidOtpException();
		}
		return otpEntity;
	}

}
