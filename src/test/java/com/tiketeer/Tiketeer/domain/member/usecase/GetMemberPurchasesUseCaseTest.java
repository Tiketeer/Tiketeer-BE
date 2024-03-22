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

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.GetMemberPurchasesCommandDto;
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
		var findPurchase1 = results.stream()
			.filter(purchase -> purchase.getPurchaseId().equals(purchase1.getId()))
			.toList();
		var findPurchase2 = results.stream()
			.filter(purchase -> purchase.getPurchaseId().equals(purchase2.getId()))
			.toList();

		// then
		Assertions.assertThat(results.size()).isEqualTo(2);
		Assertions.assertThat(findPurchase1.size()).isEqualTo(1);
		Assertions.assertThat(findPurchase2.size()).isEqualTo(1);
		Assertions.assertThat(findPurchase1.getFirst().getTitle()).isEqualTo(ticketing1.getTitle());
		Assertions.assertThat(findPurchase2.getFirst().getTitle()).isEqualTo(ticketing2.getTitle());
		Assertions.assertThat(findPurchase1.getFirst().getCount()).isEqualTo(2);
		Assertions.assertThat(findPurchase2.getFirst().getCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("멤버가 존재하지 않음 > 멤버 조회 > 실패")
	@Transactional(readOnly = true)
	void getMemberPurchasesFailNotFoundMember() {
		// given
		var mockEmail = "test@test.com";

		var command = GetMemberPurchasesCommandDto.builder().memberEmail(mockEmail).build();

		Assertions.assertThatThrownBy(() -> {
			// when
			getMemberPurchasesUseCase.getMemberPurchases(command);
			// then
		}).isInstanceOf(MemberNotFoundException.class);
	}
}
