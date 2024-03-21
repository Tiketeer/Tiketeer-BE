package com.tiketeer.Tiketeer.domain.ticketing.repository;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;

import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
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

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("멤버/티케팅/구매내역 생성 > 특정 멤버가 판매중인 티케팅 조회 > 정상 반환")
	void findTicketingWithTicketStock() {

		//given
		var now = now().truncatedTo(ChronoUnit.SECONDS);
		var member = testHelper.createMember("user@example.com", "password");
		var ticketing = ticketingRepository.save(
			new Ticketing(1000, member, "", "test", "Seoul", now, "", 600, now, now));
		var purchase = purchaseRepository.save(new Purchase(member));
		ticketRepository.save(new Ticket(null, ticketing));
		ticketRepository.save(new Ticket(purchase, ticketing));
		ticketRepository.save(new Ticket(purchase, ticketing));
		//when
		var memberTicketingSale = ticketingRepository.findTicketingWithTicketStock(member.getEmail()).getFirst();

		//then
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