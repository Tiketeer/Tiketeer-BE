package com.tiketeer.Tiketeer.domain.ticketing.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.ToString;

@ToString
@Builder
public record CreateTicketingResult(UUID ticketingId, LocalDateTime createdAt) {
}
