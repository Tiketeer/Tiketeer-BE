package com.tiketeer.Tiketeer.domain.member.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.InvalidPointChargeRequestException;
import com.tiketeer.Tiketeer.domain.member.exception.MemberIdAndAuthNotMatchedException;
import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.ChargePointCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.ChargePointResultDto;

@Service
@Transactional(readOnly = true)
public class ChargeMemberPointUseCase {
	private final MemberRepository memberRepository;

	public ChargeMemberPointUseCase(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Transactional
	public ChargePointResultDto chargePoint(ChargePointCommandDto command) {
		// TODO: 결제 모듈 붙이기
		var email = command.getEmail();
		var pointForCharge = command.getPointForCharge();

		if (pointForCharge <= 0) {
			throw new InvalidPointChargeRequestException();
		}

		var member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

		if (!member.getId().equals(command.getMemberId())) {
			throw new MemberIdAndAuthNotMatchedException();
		}

		member.setPoint(member.getPoint() + pointForCharge);

		return ChargePointResultDto.builder().totalPoint(member.getPoint()).build();
	}
}
