package com.tiketeer.Tiketeer.domain.ticket.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Limit;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@DataJpaTest
public class TicketRepositoryTest {
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
	@DisplayName("티켓 생성 및 purchase_id 할당 > purchase_id로 조회 > 성공")
	void findAllByPurchase() {
		// given
		var role = roleRepository.save(new Role(RoleEnum.SELLER));
		var member = memberRepository.save(
			new Member("test@gmail.com", "asdf1234", 0L, false, null, role));
		var now = LocalDateTime.now();
		var ticketing = ticketingRepository.save(
			new Ticketing(1000, member, "", "test", "Seoul", now, "", 600, now, now));
		var purchase = purchaseRepository.save(new Purchase(member));
		ticketRepository.save(
			new Ticket(purchase, ticketing));
		ticketRepository.save(
			new Ticket(purchase, ticketing));

		// when
		var tickets = ticketRepository.findAllByPurchase(purchase);

		// then
		assertThat(tickets.size()).isEqualTo(2);
		assertThat(tickets.get(0).getTicketing()).isEqualTo(ticketing);
		assertThat(tickets.get(0).getPurchase()).isEqualTo(purchase);
		assertThat(tickets.get(1).getTicketing()).isEqualTo(ticketing);
		assertThat(tickets.get(1).getPurchase()).isEqualTo(purchase);
	}

	@Test
	@DisplayName("티켓 생성 > ticketing_id로 purchase_id가 할당되지 않은 티켓 전체 조회 > 성공")
	void findByTicketingIdAndPurchaseIsNullOrderById() {
		// given
		var role = roleRepository.save(new Role(RoleEnum.SELLER));
		var member = memberRepository.save(
			new Member("test@gmail.com", "asdf1234", 0L, false, null, role));
		var now = LocalDateTime.now();
		var ticketing = ticketingRepository.save(
			new Ticketing(1000, member, "", "test", "Seoul", now, "", 600, now, now));
		ticketRepository.save(
			Ticket.builder().ticketing(ticketing).build());
		ticketRepository.save(
			Ticket.builder().ticketing(ticketing).build());

		// when
		var tickets = ticketRepository.findByTicketingIdAndPurchaseIsNullOrderById(ticketing.getId());

		// then
		assertThat(tickets.size()).isEqualTo(2);
		assertThat(tickets.getFirst().getTicketing()).isEqualTo(ticketing);
		assertThat(tickets.getFirst().getPurchase()).isNull();
	}

	@Test
	@DisplayName("티켓 생성 > ticketing_id로 purchase_id가 할당되지 않은 티켓 n개 조회 > 성공")
	void findByTicketingIdAndPurchaseIsNullOrderByIdWithLimit() {
		// given
		var role = roleRepository.save(new Role(RoleEnum.SELLER));
		var member = memberRepository.save(
			new Member("test@gmail.com", "asdf1234", 0L, false, null, role));
		var now = LocalDateTime.now();
		var ticketing = ticketingRepository.save(
			new Ticketing(1000, member, "", "test", "Seoul", now, "", 600, now, now));
		ticketRepository.save(
			Ticket.builder().ticketing(ticketing).build());
		ticketRepository.save(
			Ticket.builder().ticketing(ticketing).build());

		// when
		var tickets = ticketRepository.findByTicketingIdAndPurchaseIsNullOrderById(ticketing.getId(), Limit.of(1));

		// then
		assertThat(tickets.size()).isEqualTo(1);
		assertThat(tickets.getFirst().getTicketing()).isEqualTo(ticketing);
		assertThat(tickets.getFirst().getPurchase()).isNull();
	}
}
