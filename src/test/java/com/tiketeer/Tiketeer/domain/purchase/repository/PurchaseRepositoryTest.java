package com.tiketeer.Tiketeer.domain.purchase.repository;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@DataJpaTest
public class PurchaseRepositoryTest {
	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private PurchaseRepository purchaseRepository;

	@Autowired
	private TicketingRepository ticketingRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Test
	@DisplayName("멤버 구매 내역 생성 > 멤버 구매 내역 + 티켓 정보와 함께 조회 > 성공")
	@Transactional
	void findWithTicketingByMember() {
		var mockEmail = "test@test.com";
		var role = roleRepository.save(new Role(RoleEnum.SELLER));
		var member = memberRepository.save(
			new Member(mockEmail, "asdf1234", 0L, false, null, role));
		var now = LocalDateTime.now();
		var ticketing1 = ticketingRepository.save(
			new Ticketing(1000, member, "", "test1", "Seoul", now, "", 600, now, now));
		var ticketing2 = ticketingRepository.save(
			new Ticketing(1000, member, "", "test2", "Seoul", now, "", 600, now, now));
		var purchase1 = purchaseRepository.save(new Purchase(member));
		var purchase2 = purchaseRepository.save(new Purchase(member));
		ticketRepository.save(new Ticket(null, ticketing1));
		ticketRepository.save(new Ticket(purchase1, ticketing1));
		ticketRepository.save(new Ticket(purchase1, ticketing1));
		ticketRepository.save(new Ticket(purchase2, ticketing2));

		// when
		var results = purchaseRepository.findWithTicketingByMember(member);

		// then
		Assertions.assertThat(results.size()).isEqualTo(2);
		Assertions.assertThat(results.get(0).getCount()).isEqualTo(2);
		Assertions.assertThat(results.get(0).getTicketingId()).isEqualTo(ticketing1.getId());
		Assertions.assertThat(results.get(1).getCount()).isEqualTo(1);
		Assertions.assertThat(results.get(1).getTicketingId()).isEqualTo(ticketing2.getId());
	}
}
