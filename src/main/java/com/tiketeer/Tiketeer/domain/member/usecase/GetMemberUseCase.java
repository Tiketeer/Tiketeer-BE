package com.tiketeer.Tiketeer.domain.member.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.GetMemberCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.GetMemberResultDto;

@Service
@Transactional(readOnly = true)
public class GetMemberUseCase {
	private final MemberRepository memberRepository;

	public GetMemberUseCase(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	public GetMemberResultDto get(GetMemberCommandDto command) {
		var member = memberRepository.findByEmail(command.getMemberEmail()).orElseThrow(MemberNotFoundException::new);
		return GetMemberResultDto.builder()
			.email(member.getEmail())
			.createdAt(member.getCreatedAt())
			.point(member.getPoint())
			.profileUrl(member.getProfileUrl())
			.build();
	}
}
