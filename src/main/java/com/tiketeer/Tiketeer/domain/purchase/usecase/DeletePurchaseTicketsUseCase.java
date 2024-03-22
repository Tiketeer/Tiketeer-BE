package com.tiketeer.Tiketeer.domain.purchase.usecase;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.exception.PurchaseNotFoundException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.purchase.service.PurchaseService;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.DeletePurchaseTicketsCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;

@Service
public class DeletePurchaseTicketsUseCase {
	private final PurchaseRepository purchaseRepository;
	private final TicketRepository ticketRepository;
	private final PurchaseService purchaseService;

	@Autowired
	DeletePurchaseTicketsUseCase(PurchaseRepository purchaseRepository,
		TicketRepository ticketRepository, MemberRepository memberRepository, PurchaseService purchaseService) {
		this.purchaseRepository = purchaseRepository;
		this.ticketRepository = ticketRepository;
		this.purchaseService = purchaseService;

	}

	@Transactional
	public void deletePurchaseTickets(DeletePurchaseTicketsCommandDto command) {
		var purchase = purchaseRepository.findById(command.getPurchaseId()).orElseThrow(
			PurchaseNotFoundException::new);
		var ticketsUnderPurchase = purchaseService.findTicketsUnderPurchase(purchase);
		var ticketsToRefund = ticketRepository.findAllById(command.getTicketIds());
		var ticketing = ticketsUnderPurchase.getFirst().getTicketing();

		purchaseService.validatePurchaseOwnership(purchase, command.getMemberEmail());
		purchaseService.validateTicketingSalePeriod(ticketing, command.getCommandCreatedAt());

		var ticketIdUnderPurchase = ticketsUnderPurchase.stream().map(Ticket::getId).toList();
		AtomicInteger numOfDeletedTicket = new AtomicInteger();
		ticketsToRefund.forEach(ticket -> {
			if (ticketIdUnderPurchase.contains(ticket.getId())) {
				ticket.setPurchase(null);
				numOfDeletedTicket.getAndIncrement();
			}
		});
		if (numOfDeletedTicket.get() == ticketsUnderPurchase.size()) {
			purchaseRepository.delete(purchase);
		}
	}
}
