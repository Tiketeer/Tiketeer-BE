package com.tiketeer.Tiketeer.domain.member.usecase;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.exception.MemberIdAndAuthNotMatchedException;
import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.exception.NonFulfilledEventExistException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.DeleteMemberCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@Service
public class DeleteMemberUseCase {
	private final MemberRepository memberRepository;
	private final TicketingRepository ticketingRepository;

	@Autowired
	public DeleteMemberUseCase(MemberRepository memberRepository, TicketingRepository ticketingRepository) {
		this.memberRepository = memberRepository;
		this.ticketingRepository = ticketingRepository;
	}

	@Transactional
	public void deleteMember(DeleteMemberCommandDto command) {
		var member = memberRepository.findById(command.getMemberId()).orElseThrow(MemberNotFoundException::new);

		if (!member.getEmail().equals(command.getEmail())) {
			throw new MemberIdAndAuthNotMatchedException();
		}

		checkDeletable(member, command.getCommandCreatedAt());

		memberRepository.delete(member);
	}

	private void checkDeletable(Member member, LocalDateTime deletedAt) {
		var ticketingUnderMember = ticketingRepository.findAllByMember(member);
		ticketingUnderMember.forEach(ticketing -> {
			var eventFinishTime = ticketing.getEventTime().plusMinutes(ticketing.getRunningMinutes());
			if (deletedAt.isBefore(eventFinishTime)) {
				throw new NonFulfilledEventExistException();
			}
		});
	}

}
