package com.tiketeer.Tiketeer.domain.purchase.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.exception.PurchaseNotInSalePeriodException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.purchase.service.dto.CreatePurchaseCommandDto;
import com.tiketeer.Tiketeer.domain.purchase.service.dto.CreatePurchaseResultDto;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@Service
public class PurchaseService {
	private final PurchaseRepository purchaseRepository;
	private final TicketRepository ticketRepository;
	private final MemberRepository memberRepository;
	private final TicketingRepository ticketingRepository;

	@Autowired
	PurchaseService(PurchaseRepository purchaseRepository,
		TicketRepository ticketRepository, MemberRepository memberRepository, TicketingRepository ticketingRepository) {
		this.purchaseRepository = purchaseRepository;
		this.ticketRepository = ticketRepository;
		this.memberRepository = memberRepository;
		this.ticketingRepository = ticketingRepository;
	}

	@Transactional
	public CreatePurchaseResultDto createPurchase(CreatePurchaseCommandDto createPurchaseCommandDto) {
		var ticketingId = createPurchaseCommandDto.getTicketingId();
		var count = createPurchaseCommandDto.getCount();

		var member = this.memberRepository.findByEmail(createPurchaseCommandDto.getMemberEmail()).orElseThrow(
			MemberNotFoundException::new);

		validateTicketing(ticketingId, createPurchaseCommandDto.getCommandCreatedAt());

		var newPurchase = Purchase.builder().member(member).build();
		this.purchaseRepository.save(newPurchase);

		var tickets = this.ticketRepository.findByTicketingIdAndPurchaseIsNullOrderById(
			ticketingId, Limit.of(count));

		if (tickets.size() < count) {
			throw new NotEnoughTicketException();
		}

		tickets.forEach(ticket -> {
			ticket.setPurchase(newPurchase);
			this.ticketRepository.save(ticket);
		});

		return CreatePurchaseResultDto.builder()
			.purchaseId(newPurchase.getId())
			.createdAt(newPurchase.getCreatedAt())
			.build();
	}

	private void validateTicketing(UUID ticketingId, LocalDateTime now) {
		var ticketing = this.ticketingRepository.findById(ticketingId).orElseThrow(
			TicketingNotFoundException::new);
		var saleStart = ticketing.getSaleStart();
		var saleEnd = ticketing.getSaleEnd();
		if (now.isBefore(saleStart) || now.isAfter(saleEnd)) {
			throw new PurchaseNotInSalePeriodException();
		}
	}
}
