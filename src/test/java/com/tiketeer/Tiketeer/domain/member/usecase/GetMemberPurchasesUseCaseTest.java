package com.tiketeer.Tiketeer.domain.member.usecase;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.GetMemberPurchasesCommandDto;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class GetMemberPurchasesUseCaseTest {
	@Autowired
	private TestHelper testHelper;

	@Autowired
	private GetMemberPurchasesUseCase getMemberPurchasesUseCase;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private TicketRepository ticketRepository;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("정상 조건 > 멤버 구매 내역 조회 요청 > 성공")
	@Transactional
	void getMemberPurchases() {
		// given
		var mockEmail = "test@test.com";
		var member = testHelper.createMember(mockEmail);
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
		var results = getMemberPurchasesUseCase.getMemberPurchases(
			GetMemberPurchasesCommandDto.builder().memberEmail(mockEmail).build());

		// then
		Assertions.assertThat(results.size()).isEqualTo(2);
		Assertions.assertThat(results.get(0).getCount()).isEqualTo(2);
		Assertions.assertThat(results.get(0).getTicketingId()).isEqualTo(ticketing1.getId());
		Assertions.assertThat(results.get(1).getCount()).isEqualTo(1);
		Assertions.assertThat(results.get(1).getTicketingId()).isEqualTo(ticketing2.getId());
	}
}
