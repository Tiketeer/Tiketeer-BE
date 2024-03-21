package com.tiketeer.Tiketeer.domain.ticket.service;

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
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
@DisplayName("TicketService 테스트")
public class TicketServiceTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private TicketService ticketService;
	@Autowired
	private TicketingService ticketingService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private TicketRepository ticketRepository;
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
	@DisplayName("존재하지 않는 티케팅 > 티케팅 하위 티켓 리스트 호출 > 실패")
	void listTicketByTicketingIdFailBecauseTicketingNotExist() {
		// given
		var mockUuid = UUID.randomUUID();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketService.listTicketByTicketingId(mockUuid);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("유효한 티케팅 > 티케팅 하위 티켓 리스트 호출 > 정상 동작")
	@Transactional
	void listTicketByTicketingSuccess() {
		// given
		var now = LocalDateTime.now();

		var mockEmail = "test@test.com";
		var member = testHelper.createMember(mockEmail);

		var mockStock = 30;

		var ticketingId = createTicketingAndReturnId(member, mockStock, now.plusYears(1), now.plusYears(2),
			now.plusYears(3));

		// when
		var tickets = ticketService.listTicketByTicketingId(ticketingId);

		// then
		Assertions.assertThat(tickets.size()).isEqualTo(mockStock);
	}

	@Test
	@DisplayName("유효하지 않은 티케팅 > 티켓 생성 요청 > 실패")
	void createTicketsFailBecauseNotExistTicketing() {
		// given
		var invalidTicketingId = UUID.randomUUID();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketService.createTickets(invalidTicketingId, 100);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("유효한 티케팅 (기존 티켓 10) > 추가 하위 티켓 생성 요청 (20) > 성공 및 총 재고 10 + 20")
	void createTicketsSuccess() {
		// given
		var now = LocalDateTime.now();

		var mockEmail = "test@test.com";
		var member = testHelper.createMember(mockEmail);

		var mockStock = 10;
		var ticketingId = createTicketingAndReturnId(member, mockStock, now.plusYears(1), now.plusYears(2),
			now.plusYears(3));

		var addTickets = 20;

		// when
		ticketService.createTickets(ticketingId, addTickets);

		// then
		var tickets = ticketService.listTicketByTicketingId(ticketingId);
		Assertions.assertThat(tickets.size()).isEqualTo(mockStock + addTickets);
	}

	private UUID createTicketingAndReturnId(Member member, int stock, LocalDateTime saleStart, LocalDateTime saleEnd,
		LocalDateTime eventTime) {
		var ticketing = ticketingRepository.save(Ticketing.builder()
			.member(member)
			.title("음악회")
			.location("서울 강남역 8번 출구")
			.category("음악회")
			.runningMinutes(100)
			.price(10000L)
			.saleStart(saleStart)
			.saleEnd(saleEnd)
			.eventTime(eventTime).build());
		ticketRepository.saveAll(Arrays.stream(new int[stock])
			.mapToObj(i -> Ticket.builder().ticketing(ticketing).build())
			.toList());
		return ticketing.getId();
	}
}
