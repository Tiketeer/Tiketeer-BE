package com.tiketeer.Tiketeer.domain.member.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.usecase.dto.GetMemberTicketingSalesCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.GetMemberTicketingSalesResultDto;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@Service
@Transactional(readOnly = true)
public class GetMemberTicketingSalesUseCase {

	private final TicketingRepository ticketingRepository;

	public GetMemberTicketingSalesUseCase(TicketingRepository ticketingRepository) {
		this.ticketingRepository = ticketingRepository;
	}

	public List<GetMemberTicketingSalesResultDto> getMemberTicketingSales(GetMemberTicketingSalesCommandDto command) {
		return ticketingRepository.findTicketingWithTicketStock(
			command.getEmail());
	}
}
