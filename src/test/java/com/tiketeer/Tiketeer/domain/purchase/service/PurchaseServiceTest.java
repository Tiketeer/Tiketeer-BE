package com.tiketeer.Tiketeer.domain.purchase.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Limit;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.exception.AccessForNotOwnedPurchaseException;
import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.exception.PurchaseNotInSalePeriodException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class PurchaseServiceTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private PurchaseService purchaseService;
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private RoleRepository roleRepository;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("티케팅 판매 기간임 > 티케팅 판매 기간 테스트 > 통과")
	@Transactional
	void validateTicketingSalePeriodSuccess() {
		// given
		var mockEmail = "test1@test.com";
		var member = testHelper.createMember(mockEmail, "1234");
		var ticketing = createTicketing(member, 0, 1);

		// when
		// then
		Assertions.assertThatNoException().isThrownBy(() -> {
			purchaseService.validateTicketingSalePeriod(ticketing.getId(), LocalDateTime.now());
		});
	}

	@Test
	@DisplayName("티케팅 판매 기간이 아님 > 티케팅 판매 기간 테스트 > throw err")
	@Transactional
	void validateTicketingSalePeriodThrowErr() {
		// given
		var mockEmail = "test1@test.com";
		var member = testHelper.createMember(mockEmail, "1234");
		var ticketing = createTicketing(member, 1, 1);

		Assertions.assertThatThrownBy(() -> {
			// when
			purchaseService.validateTicketingSalePeriod(ticketing.getId(), LocalDateTime.now());
			// then
		}).isInstanceOf(PurchaseNotInSalePeriodException.class);
	}

	@Test
	@DisplayName("구매자 본인이 접근 > 구매자 소유 검증 > 통과")
	void validatePurchaseOwnershipSuccess() {
		// given
		var mockEmail = "test1@test.com";
		var member = testHelper.createMember(mockEmail, "1234");
		var ticketing = createTicketing(member, 0, 1);
		var purchaseTicketPair = createPurchase(member, ticketing, 1);
		var purchase = purchaseTicketPair.getFirst();

		// when
		// then
		Assertions.assertThatNoException().isThrownBy(() -> {
			purchaseService.validatePurchaseOwnership(purchase.getId(), mockEmail);
		});
	}

	@Test
	@DisplayName("구매자가 아닌 사람이 접근 > 구매자 소유 검증 > Throw Err")
	void validatePurchaseOwnershipThrowErr() {
		// given
		var mockEmail = "test1@test.com";
		var member = testHelper.createMember(mockEmail, "1234");
		var ticketing = createTicketing(member, 0, 1);
		var purchaseTicketPair = createPurchase(member, ticketing, 1);
		var purchase = purchaseTicketPair.getFirst();

		Assertions.assertThatThrownBy(() -> {
			// when
			purchaseService.validatePurchaseOwnership(purchase.getId(), "other@test.com");
			// then
		}).isInstanceOf(AccessForNotOwnedPurchaseException.class);
	}

	@Test
	@DisplayName("구매 생성 > 구매 하위 티켓 조회 요청 > 성공")
	void findTicketsUnderPurchaseSuccess() {
		// given
		var mockEmail = "test1@test.com";
		var member = testHelper.createMember(mockEmail, "1234");
		var ticketing = createTicketing(member, 0, 1);
		var purchaseTicketPair = createPurchase(member, ticketing, 1);
		var purchase = purchaseTicketPair.getFirst();
		var tickets = purchaseTicketPair.getSecond();

		// when
		var ticketsUnderPurchase = purchaseService.findTicketsUnderPurchase(purchase.getId());

		// then
		Assertions.assertThat(tickets.getFirst().getId()).isEqualTo(ticketsUnderPurchase.getFirst().getId());
		Assertions.assertThat(tickets.getFirst().getPurchase().getId())
			.isEqualTo(ticketsUnderPurchase.getFirst().getPurchase().getId());
		Assertions.assertThat(tickets.getFirst().getTicketing().getId())
			.isEqualTo(ticketsUnderPurchase.getFirst().getTicketing().getId());
		Assertions.assertThat(tickets.getFirst().getCreatedAt())
			.isEqualTo(ticketsUnderPurchase.getFirst().getCreatedAt());
	}

	private Ticketing createTicketing(Member member, int saleStartAfterYears, int stock) {
		var now = LocalDateTime.now();
		var eventTime = now.plusYears(saleStartAfterYears + 2);
		var saleStart = now.plusYears(saleStartAfterYears);
		var saleEnd = now.plusYears(saleStartAfterYears + 1);
		var ticketing = ticketingRepository.save(Ticketing.builder()
			.price(1000)
			.title("test")
			.member(member)
			.description("")
			.location("Seoul")
			.eventTime(eventTime)
			.saleStart(saleStart)
			.saleEnd(saleEnd)
			.category("concert")
			.runningMinutes(300).build());
		ticketRepository.saveAll(Arrays.stream(new int[stock])
			.mapToObj(i -> Ticket.builder().ticketing(ticketing).build())
			.toList());
		return ticketing;
	}

	private Pair<Purchase, List<Ticket>> createPurchase(Member member, Ticketing ticketing, int count) {
		var purchase = this.purchaseRepository.save(Purchase.builder().member(member).build());

		if (count > 0) {
			var tickets = updateTicketPurchase(purchase, ticketing, count);
			return Pair.of(purchase, tickets);
		}
		return Pair.of(purchase, Collections.emptyList());
	}

	private List<Ticket> updateTicketPurchase(Purchase purchase, Ticketing ticketing, int count) {
		var tickets = this.ticketRepository.findByTicketingIdAndPurchaseIsNullOrderById(ticketing.getId(),
			Limit.of(count));
		if (tickets.size() < count) {
			throw new NotEnoughTicketException();
		}
		tickets.forEach(ticket -> {
			ticket.setPurchase(purchase);
			this.ticketRepository.save(ticket);
		});
		return tickets;
	}
}