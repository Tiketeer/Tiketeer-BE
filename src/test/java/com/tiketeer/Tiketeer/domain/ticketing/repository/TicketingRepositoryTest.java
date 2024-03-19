package com.tiketeer.Tiketeer.domain.ticketing.repository;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;

import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
class TicketingRepositoryTest {
	@Autowired
	private TestHelper testHelper;
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
	@Autowired
	private PasswordEncoder passwordEncoder;

	private Member saveMember(String email, String password) {
		Role role = roleRepository.findByName(RoleEnum.BUYER).orElseThrow();
		Member member = Member.builder()
			.email(email)
			.password(passwordEncoder.encode(password))
			.point(0)
			.enabled(true)
			.role(role)
			.build();
		return memberRepository.save(member);
	}

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	void findTicketingWithTicketStock() {
		var now = now().truncatedTo(ChronoUnit.SECONDS);
		var member = saveMember("user@example.com", "password");
		var ticketing = ticketingRepository.save(
			new Ticketing(1000, member, "", "test", "Seoul", now, "", 600, now, now));
		var purchase = purchaseRepository.save(new Purchase(member));
		ticketRepository.save(new Ticket(null, ticketing));
		ticketRepository.save(new Ticket(purchase, ticketing));
		ticketRepository.save(new Ticket(purchase, ticketing));

		var memberTicketingSale = ticketingRepository.findTicketingWithTicketStock(member.getEmail()).getFirst();

		assertThat(memberTicketingSale.getTitle()).isEqualTo("test");
		assertThat(memberTicketingSale.getLocation()).isEqualTo("Seoul");
		assertThat(memberTicketingSale.getRunningMinutes()).isEqualTo(600);
		assertThat(memberTicketingSale.getDescription()).isEqualTo("");
		assertThat(memberTicketingSale.getSaleStart()).isEqualTo(now);
		assertThat(memberTicketingSale.getSaleEnd()).isEqualTo(now);

		assertThat(memberTicketingSale.getRemainStock()).isEqualTo(1);
		assertThat(memberTicketingSale.getStock()).isEqualTo(3);

	}
}