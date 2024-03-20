package com.tiketeer.Tiketeer.domain.ticket.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
	List<Ticket> findAllByTicketing(Ticketing ticketing);

	List<Ticket> findAllByPurchase(Purchase purchase);

	List<Ticket> findByTicketingIdAndPurchaseIsNull(UUID ticketingId);

	List<Ticket> findByTicketingIdAndPurchaseIsNullOrderById(UUID ticketingId, Limit limit);
}
