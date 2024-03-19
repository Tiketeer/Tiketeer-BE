package com.tiketeer.Tiketeer.domain.purchase.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.exception.AccessForNotOwnedPurchaseException;
import com.tiketeer.Tiketeer.domain.purchase.exception.EmptyPurchaseException;
import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.exception.PurchaseNotFoundException;
import com.tiketeer.Tiketeer.domain.purchase.exception.PurchaseNotInSalePeriodException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.purchase.service.dto.CreatePurchaseCommandDto;
import com.tiketeer.Tiketeer.domain.purchase.service.dto.CreatePurchaseResultDto;
import com.tiketeer.Tiketeer.domain.purchase.service.dto.DeletePurchaseTicketsCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
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

		validateTicketingSalePeriod(ticketingId, createPurchaseCommandDto.getCommandCreatedAt());

		var newPurchase = this.purchaseRepository.save(Purchase.builder().member(member).build());

		var tickets = this.ticketRepository.findByTicketingIdAndPurchaseIsNullOrderById(
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

	@Transactional
	public void deletePurchaseTickets(DeletePurchaseTicketsCommandDto deletePurchaseTicketsCommandDto) {
		var purchase = this.purchaseRepository.findById(deletePurchaseTicketsCommandDto.getPurchaseId()).orElseThrow(
			PurchaseNotFoundException::new);
		var ticketsUnderPurchase = findTicketsUnderPurchase(purchase);
		var ticketsToRefund = this.ticketRepository.findAllById(deletePurchaseTicketsCommandDto.getTicketIds());
		var ticketing = ticketsUnderPurchase.getFirst().getTicketing();

		validatePurchaseOwnership(purchase, deletePurchaseTicketsCommandDto.getMemberEmail());
		validateTicketingSalePeriod(ticketing, deletePurchaseTicketsCommandDto.getCommandCreatedAt());

		var ticketIdUnderPurchase = ticketsUnderPurchase.stream().map(Ticket::getId).toList();
		AtomicInteger numOfDeletedTicket = new AtomicInteger();
		ticketsToRefund.forEach(ticket -> {
			if (ticketIdUnderPurchase.contains(ticket.getId())) {
				ticket.setPurchase(null);
				numOfDeletedTicket.getAndIncrement();
			}
		});
		if (numOfDeletedTicket.get() == ticketsUnderPurchase.size()) {
			this.purchaseRepository.delete(purchase);
		}
	}

	private void validateTicketingSalePeriod(UUID ticketingId, LocalDateTime now) {
		var ticketing = this.ticketingRepository.findById(ticketingId).orElseThrow(
			TicketingNotFoundException::new);
		var saleStart = ticketing.getSaleStart();
		var saleEnd = ticketing.getSaleEnd();
		if (now.isBefore(saleStart) || now.isAfter(saleEnd)) {
			throw new PurchaseNotInSalePeriodException();
		}
	}

	private void validateTicketingSalePeriod(Ticketing ticketing, LocalDateTime now) {
		var saleStart = ticketing.getSaleStart();
		var saleEnd = ticketing.getSaleEnd();
		if (now.isBefore(saleStart) || now.isAfter(saleEnd)) {
			throw new PurchaseNotInSalePeriodException();
		}
	}

	private void validatePurchaseOwnership(Purchase purchase, String email) {
		if (!purchase.getMember().getEmail().equals(email)) {
			throw new AccessForNotOwnedPurchaseException();
		}
	}

	private List<Ticket> findTicketsUnderPurchase(Purchase purchase) {
		var ticketsUnderPurchase = this.ticketRepository.findAllByPurchase(purchase);
		if (ticketsUnderPurchase.isEmpty()) {
			throw new EmptyPurchaseException();
		}
		return ticketsUnderPurchase;
	}
}
