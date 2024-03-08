package com.tiketeer.Tiketeer.domain.ticketing.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;

@Repository
public interface TicketingRepository extends JpaRepository<Ticketing, UUID> {
}
