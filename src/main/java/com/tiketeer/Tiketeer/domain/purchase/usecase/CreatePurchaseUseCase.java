package com.tiketeer.Tiketeer.domain.purchase.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.purchase.service.PurchaseService;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseCommandDto;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.CreatePurchaseResultDto;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;

@Service
public class CreatePurchaseUseCase {
	private final PurchaseRepository purchaseRepository;
	private final TicketRepository ticketRepository;
	private final MemberRepository memberRepository;
	private final PurchaseService purchaseService;

	@Autowired
	CreatePurchaseUseCase(PurchaseRepository purchaseRepository,
		TicketRepository ticketRepository, MemberRepository memberRepository, PurchaseService purchaseService) {
		this.purchaseRepository = purchaseRepository;
		this.ticketRepository = ticketRepository;
		this.memberRepository = memberRepository;
		this.purchaseService = purchaseService;

	}

	@Transactional
	public CreatePurchaseResultDto createPurchase(CreatePurchaseCommandDto command) {
		var ticketingId = command.getTicketingId();
		var count = command.getCount();

		var member = memberRepository.findByEmail(command.getMemberEmail()).orElseThrow(
			MemberNotFoundException::new);

		purchaseService.validateTicketingSalePeriod(ticketingId, command.getCommandCreatedAt());

		var newPurchase = purchaseRepository.save(Purchase.builder().member(member).build());

		var tickets = ticketRepository.findByTicketingIdAndPurchaseIsNullOrderById(
			ticketingId, Limit.of(count));

		if (tickets.size() < count) {
			throw new NotEnoughTicketException();
		}

		tickets.forEach(ticket -> {
			ticket.setPurchase(newPurchase);
		});

		return CreatePurchaseResultDto.builder()
			.purchaseId(newPurchase.getId())
			.createdAt(newPurchase.getCreatedAt())
			.build();
	}
}
