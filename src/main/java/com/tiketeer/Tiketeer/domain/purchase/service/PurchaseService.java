package com.tiketeer.Tiketeer.domain.purchase.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.exception.AccessForNotOwnedPurchaseException;
import com.tiketeer.Tiketeer.domain.purchase.exception.EmptyPurchaseException;
import com.tiketeer.Tiketeer.domain.purchase.exception.PurchaseNotInSalePeriodException;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@Service
public class PurchaseService {
	private final TicketRepository ticketRepository;
	private final TicketingRepository ticketingRepository;

	@Autowired
	PurchaseService(TicketRepository ticketRepository, TicketingRepository ticketingRepository) {
		this.ticketRepository = ticketRepository;
		this.ticketingRepository = ticketingRepository;
	}

	@Transactional(readOnly = true)
	public void validateTicketingSalePeriod(UUID ticketingId, LocalDateTime now) {
		var ticketing = ticketingRepository.findById(ticketingId).orElseThrow(
			TicketingNotFoundException::new);
		validateTicketingSalePeriod(ticketing, now);
	}

	public void validateTicketingSalePeriod(Ticketing ticketing, LocalDateTime now) {
		var saleStart = ticketing.getSaleStart();
		var saleEnd = ticketing.getSaleEnd();
		if (now.isBefore(saleStart) || now.isAfter(saleEnd)) {
			throw new PurchaseNotInSalePeriodException();
		}
	}

	public void validatePurchaseOwnership(Purchase purchase, String email) {
		if (!purchase.getMember().getEmail().equals(email)) {
			throw new AccessForNotOwnedPurchaseException();
		}
	}

	@Transactional(readOnly = true)
	public List<Ticket> findTicketsUnderPurchase(Purchase purchase) {
		var ticketsUnderPurchase = ticketRepository.findAllByPurchase(purchase);
		if (ticketsUnderPurchase.isEmpty()) {
			throw new EmptyPurchaseException();
		}
		return ticketsUnderPurchase;
	}
}
