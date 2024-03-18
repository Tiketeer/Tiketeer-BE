package com.tiketeer.Tiketeer.domain.purchase.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.exception.PurchaseNotInSalePeriodException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.purchase.service.dto.CreatePurchaseCommandDto;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class PurchaseServiceTest {
	private final TestHelper testHelper;
	private final PurchaseService purchaseService;
	private final PurchaseRepository purchaseRepository;
	private final TicketRepository ticketRepository;
	private final MemberRepository memberRepository;
	private final TicketingRepository ticketingRepository;
	private final RoleRepository roleRepository;

	@Autowired
	PurchaseServiceTest(TestHelper testHelper, PurchaseService purchaseService, PurchaseRepository purchaseRepository,
		TicketRepository ticketRepository, MemberRepository memberRepository, TicketingRepository ticketingRepository,
		RoleRepository roleRepository) {
		this.testHelper = testHelper;
		this.purchaseService = purchaseService;
		this.purchaseRepository = purchaseRepository;
		this.ticketRepository = ticketRepository;
		this.memberRepository = memberRepository;
		this.ticketingRepository = ticketingRepository;
		this.roleRepository = roleRepository;
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
	@DisplayName("정상 조건 > 구매 생성 요청 > 성공")
	void createPurchaseSuccess() {
		// given
		var mockEmail = "test1@test.com";
		var member = createMember(mockEmail, "1234");
		var ticketing = createTicketing(member.getId(), 0, 1);

		var createPurchaseCommand = CreatePurchaseCommandDto.builder()
			.memberEmail(mockEmail)
			.ticketingId(ticketing.getId())
			.count(1)
			.build();

		// when
		var result = purchaseService.createPurchase(createPurchaseCommand);

		// then
		var purchaseOptional = purchaseRepository.findById(result.getPurchaseId());
		Assertions.assertThat(purchaseOptional.isPresent()).isTrue();

		var tickets = ticketRepository.findAllByPurchase(purchaseOptional.get());
		Assertions.assertThat(tickets.size()).isEqualTo(createPurchaseCommand.getCount());
	}

	@Test
	@DisplayName("존재하지 않는 멤버 > 구매 생성 요청 > 실패")
	void createPurchaseFailMemberNotFound() {
		// given
		var mockEmail = "test1@test.com";

		var createPurchaseCommand = CreatePurchaseCommandDto.builder()
			.memberEmail(mockEmail)
			.ticketingId(UUID.randomUUID())
			.count(1)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			purchaseService.createPurchase(createPurchaseCommand);
			// then
		}).isInstanceOf(MemberNotFoundException.class);
	}

	@Test
	@DisplayName("티케팃 판매 기간이 아님 > 구매 생성 요청 > 실패")
	void createPurchaseFailNotInSalePeriod() {
		// given
		var mockEmail = "test1@test.com";
		var member = createMember(mockEmail, "1234");
		var ticketing = createTicketing(member.getId(), 1, 1);

		var createPurchaseCommand = CreatePurchaseCommandDto.builder()
			.memberEmail(mockEmail)
			.ticketingId(ticketing.getId())
			.count(1)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			purchaseService.createPurchase(createPurchaseCommand);
			// then
		}).isInstanceOf(PurchaseNotInSalePeriodException.class);
	}

	@Test
	@DisplayName("구매 가능한 티켓이 부족 > 구매 생성 요청 > 실패")
	void createPurchaseFailNotEnoughTicket() {
		// given
		var mockEmail = "test1@test.com";
		var member = createMember(mockEmail, "1234");
		var ticketing = createTicketing(member.getId(), 0, 1);

		var createPurchaseCommand = CreatePurchaseCommandDto.builder()
			.memberEmail(mockEmail)
			.ticketingId(ticketing.getId())
			.count(2)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			purchaseService.createPurchase(createPurchaseCommand);
			// then
		}).isInstanceOf(NotEnoughTicketException.class);
	}

	public Member createMember(String email, String password) {
		var role = roleRepository.findByName(RoleEnum.SELLER).orElseThrow();
		var memberForSave = Member.builder()
			.email(email)
			.password(password).role(role).build();
		return memberRepository.save(memberForSave);
	}

	private Ticketing createTicketing(UUID memberId, int saleStartAfterYears, int stock) {
		var member = memberRepository.findById(memberId).orElseThrow();

		var now = LocalDateTime.now();
		var eventTime = now.plusYears(saleStartAfterYears + 2);
		var saleStart = now.plusYears(saleStartAfterYears);
		var saleEnd = now.plusYears(saleStartAfterYears + 1);
		var ticketing = Ticketing.builder()
			.price(1000)
			.title("test")
			.member(member)
			.description("")
			.location("Seoul")
			.eventTime(eventTime)
			.saleStart(saleStart)
			.saleEnd(saleEnd)
			.category("concert")
			.runningMinutes(300).build();
		ticketingRepository.save(ticketing);
		ticketRepository.saveAll(Arrays.stream(new int[stock])
			.mapToObj(i -> Ticket.builder().ticketing(ticketing).build())
			.toList());
		return ticketing;
	}
}
