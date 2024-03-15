package com.tiketeer.Tiketeer.domain.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.InvalidOtpException;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.AuthMemberWithEmailOtpCommandDto;

@Service
@Transactional(readOnly = true)
public class MemberService {
	private final OtpRepository otpRepository;

	@Autowired
	public MemberService(OtpRepository otpRepository) {
		this.otpRepository = otpRepository;
	}

	@Transactional
	public void authMemberWithEmailOtp(AuthMemberWithEmailOtpCommandDto command) {
		var otp = otpRepository.findById(command.getOtp()).orElseThrow(InvalidOtpException::new);
		var member = otp.getMember();
		member.setEnabled(true);
		otpRepository.delete(otp);
	}
}
