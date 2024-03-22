package com.tiketeer.Tiketeer.domain.purchase.usecase;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
import com.tiketeer.Tiketeer.domain.purchase.exception.EmptyPurchaseException;
import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.exception.PurchaseNotFoundException;
import com.tiketeer.Tiketeer.domain.purchase.exception.PurchaseNotInSalePeriodException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.purchase.usecase.dto.DeletePurchaseTicketsCommandDto;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class DeletePurchaseTicketsUseCaseTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private DeletePurchaseTicketsUseCase deletePurchaseTicketsUseCase;
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
	@DisplayName("구매 내역 일부 환불 > 티켓 환불 요청 > 성공")
	@Transactional
	void deletePurchaseTicketsSuccess() {
		// given
		var mockEmail = "test1@test.com";
		var member = testHelper.createMember(mockEmail, "1234");
		var ticketing = createTicketing(member, 0, 5);
		var purchaseTicketPair = createPurchase(member, ticketing, 2);
		var purchase = purchaseTicketPair.getFirst();
		var tickets = purchaseTicketPair.getSecond();

		List<UUID> ticketsToRefund = Collections.singletonList(tickets.getFirst().getId());
		var deletePurchaseCommand = DeletePurchaseTicketsCommandDto.builder()
			.memberEmail(mockEmail)
			.purchaseId(purchase.getId())
			.ticketIds(ticketsToRefund)
			.build();

		// when
		deletePurchaseTicketsUseCase.deletePurchaseTickets(deletePurchaseCommand);

		// then
		var purchaseInDbOpt = purchaseRepository.findById(purchase.getId());
		Assertions.assertThat(purchaseInDbOpt.isPresent()).isTrue();

		var ticketsUnderPurchase = ticketRepository.findAllByPurchase(purchase);
		Assertions.assertThat(ticketsUnderPurchase.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("구매 내역 전체 환불 > 티켓 환불 요청 > 성공")
	@Transactional
	void deletePurchaseAllTicketsSuccess() {
		// given
		var mockEmail = "test1@test.com";
		var member = testHelper.createMember(mockEmail, "1234");
		var ticketing = createTicketing(member, 0, 5);
		var purchaseTicketPair = createPurchase(member, ticketing, 2);
		var purchase = purchaseTicketPair.getFirst();
		var tickets = purchaseTicketPair.getSecond();

		List<UUID> ticketsToRefund = tickets.stream().map(Ticket::getId).toList();
		var deletePurchaseCommand = DeletePurchaseTicketsCommandDto.builder()
			.memberEmail(mockEmail)
			.purchaseId(purchase.getId())
			.ticketIds(ticketsToRefund)
			.build();

		// when
		deletePurchaseTicketsUseCase.deletePurchaseTickets(deletePurchaseCommand);

		// then
		var purchaseInDbOpt = purchaseRepository.findById(purchase.getId());
		Assertions.assertThat(purchaseInDbOpt.isPresent()).isFalse();

		var ticketsUnderPurchase = ticketRepository.findAllByPurchase(purchase);
		Assertions.assertThat(ticketsUnderPurchase.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("구매 내역이 존재하지 않음 > 티켓 환불 요청 > 실패")
	@Transactional
	void deletePurchaseTicketsFailPurchaseNotFound() {
		// given
		var mockEmail = "test1@test.com";
		testHelper.createMember(mockEmail, "1234");

		List<UUID> ticketsToRefund = Collections.singletonList(UUID.randomUUID());
		var deletePurchaseCommand = DeletePurchaseTicketsCommandDto.builder()
			.memberEmail(mockEmail)
			.purchaseId(UUID.randomUUID())
			.ticketIds(ticketsToRefund)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			deletePurchaseTicketsUseCase.deletePurchaseTickets(deletePurchaseCommand);
			// then
		}).isInstanceOf(PurchaseNotFoundException.class);
	}

	@Test
	@DisplayName("빈 구매 내역 > 티켓 환불 요청 > 실패")
	@Transactional
	void deletePurchaseTicketsFailEmptyPurchase() {
		// given
		var mockEmail = "test1@test.com";
		var member = testHelper.createMember(mockEmail, "1234");
		var ticketing = createTicketing(member, 0, 5);
		var purchaseTicketPair = createPurchase(member, ticketing, 0);
		var purchase = purchaseTicketPair.getFirst();

		List<UUID> ticketsToRefund = Collections.singletonList(UUID.randomUUID());
		var deletePurchaseCommand = DeletePurchaseTicketsCommandDto.builder()
			.memberEmail(mockEmail)
			.purchaseId(purchase.getId())
			.ticketIds(ticketsToRefund)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			deletePurchaseTicketsUseCase.deletePurchaseTickets(deletePurchaseCommand);
			// then
		}).isInstanceOf(EmptyPurchaseException.class);
	}

	@Test
	@DisplayName("구매 내역 소유자가 아님 > 티켓 환불 요청 > 실패")
	@Transactional
	void deletePurchaseTicketsFailNotPurchaseOwner() {
		// given
		var mockEmail = "test1@test.com";
		var member = testHelper.createMember(mockEmail, "1234");
		var ticketing = createTicketing(member, 0, 5);
		var purchaseTicketPair = createPurchase(member, ticketing, 1);
		var purchase = purchaseTicketPair.getFirst();
		var tickets = purchaseTicketPair.getSecond();

		List<UUID> ticketsToRefund = Collections.singletonList(tickets.getFirst().getId());
		var deletePurchaseCommand = DeletePurchaseTicketsCommandDto.builder()
			.memberEmail("another@test.com")
			.purchaseId(purchase.getId())
			.ticketIds(ticketsToRefund)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			deletePurchaseTicketsUseCase.deletePurchaseTickets(deletePurchaseCommand);
			// then
		}).isInstanceOf(AccessForNotOwnedPurchaseException.class);
	}

	@Test
	@DisplayName("티켓팅 판매 기간이 아님 > 티켓 환불 요청 > 실패")
	@Transactional
	void deletePurchaseTicketsFailNotTicketingSalePeriod() {
		// given
		var mockEmail = "test1@test.com";
		var member = testHelper.createMember(mockEmail, "1234");
		var ticketing = createTicketing(member, -2, 1);
		var purchaseTicketPair = createPurchase(member, ticketing, 1);
		var purchase = purchaseTicketPair.getFirst();
		var tickets = purchaseTicketPair.getSecond();

		List<UUID> ticketsToRefund = Collections.singletonList(tickets.getFirst().getId());
		var deletePurchaseCommand = DeletePurchaseTicketsCommandDto.builder()
			.memberEmail(mockEmail)
			.purchaseId(purchase.getId())
			.ticketIds(ticketsToRefund)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			deletePurchaseTicketsUseCase.deletePurchaseTickets(deletePurchaseCommand);
			// then
		}).isInstanceOf(PurchaseNotInSalePeriodException.class);
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
