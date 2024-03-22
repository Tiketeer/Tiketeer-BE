package com.tiketeer.Tiketeer.domain.member.usecase;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.GetMemberPurchasesCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.GetMemberPurchasesResultDto;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;

@Service
@Transactional(readOnly = true)
public class GetMemberPurchasesUseCase {
	private final MemberRepository memberRepository;
	private final PurchaseRepository purchaseRepository;

	@Autowired
	public GetMemberPurchasesUseCase(MemberRepository memberRepository, PurchaseRepository purchaseRepository) {
		this.memberRepository = memberRepository;
		this.purchaseRepository = purchaseRepository;
	}

	public List<GetMemberPurchasesResultDto> getMemberPurchases(GetMemberPurchasesCommandDto command) {
		var member = memberRepository.findByEmail(command.getMemberEmail()).orElseThrow(MemberNotFoundException::new);
		return purchaseRepository.findWithTicketingByMember(member);
	}
}
