package com.tiketeer.Tiketeer.domain.member.service;

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

import com.tiketeer.Tiketeer.domain.member.service.dto.GetMemberTicketingSalesCommandDto;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
class MemberTicketingServiceTest {

	@Autowired
	private TestHelper testHelper;
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private MemberTicketingService memberTicketingService;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("판매중인 티켓 및 티케팅 > 판매중인 티케팅 목록 요청 > 반환 후 성공")
	void getMemberTicketingSalesSuccess() {

		//given
		var now = now().truncatedTo(ChronoUnit.SECONDS);
		var member = testHelper.createMember("user@example.com");
		var ticketing = ticketingRepository.save(
			new Ticketing(1000, member, "", "test", "Seoul", now, "", 600, now, now));
		var purchase = purchaseRepository.save(new Purchase(member));
		ticketRepository.save(new Ticket(null, ticketing));
		ticketRepository.save(new Ticket(purchase, ticketing));
		ticketRepository.save(new Ticket(purchase, ticketing));

		//when
		var memberTicketingSale = memberTicketingService.getMemberTicketingSales(
			new GetMemberTicketingSalesCommandDto(member.getId(), member.getEmail())).getFirst();

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