package com.tiketeer.Tiketeer.domain.ticket.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiketeer.Tiketeer.domain.ticket.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
}
