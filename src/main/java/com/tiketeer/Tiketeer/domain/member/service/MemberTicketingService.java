package com.tiketeer.Tiketeer.domain.member.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.service.dto.GetMemberTicketingSalesCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.GetMemberTicketingSalesResultDto;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@Service
@Transactional(readOnly = true)
public class MemberTicketingService {

	private final TicketingRepository ticketingRepository;

	public MemberTicketingService(TicketingRepository ticketingRepository, TicketRepository ticketRepository) {
		this.ticketingRepository = ticketingRepository;
	}

	public List<GetMemberTicketingSalesResultDto> getMemberTicketingSales(GetMemberTicketingSalesCommandDto command) {
		return ticketingRepository.findTicketingWithTicketStock(
			command.getEmail());
	}

}
