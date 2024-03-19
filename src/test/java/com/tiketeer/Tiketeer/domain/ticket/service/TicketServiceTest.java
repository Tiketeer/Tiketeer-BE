package com.tiketeer.Tiketeer.domain.ticket.service;

import java.time.LocalDateTime;
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
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.service.dto.CreateTicketCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.service.dto.DropAllTicketsUnderSomeTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.service.dto.DropNumOfTicketsUnderSomeTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticket.service.dto.ListTicketByTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.UpdateTicketingAfterSaleStartException;
import com.tiketeer.Tiketeer.domain.ticketing.service.TicketingService;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
@DisplayName("TicketService 테스트")
public class TicketServiceTest {
	private final TestHelper testHelper;
	private final TicketService ticketService;
	private final TicketingService ticketingService;
	private final MemberRepository memberRepository;
	private final RoleRepository roleRepository;

	@Autowired
	public TicketServiceTest(TestHelper testHelper, TicketService ticketService, TicketingService ticketingService,
		MemberRepository memberRepository,
		RoleRepository roleRepository) {
		this.testHelper = testHelper;
		this.ticketService = ticketService;
		this.ticketingService = ticketingService;
		this.memberRepository = memberRepository;
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
	@DisplayName("존재하지 않는 티케팅 > 티케팅 하위 티켓 리스트 호출 > 실패")
	void listTicketByTicketingFailBecauseTicketingNotExist() {
		// given
		var mockUuid = UUID.randomUUID();
		var command = new ListTicketByTicketingCommandDto(mockUuid);

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketService.listTicketByTicketing(command);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("유효한 티케팅 > 티케팅 하위 티켓 리스트 호출 > 정상 동작")
	void listTicketByTicketingSuccess() {
		// given
		var now = LocalDateTime.now();

		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var mockStock = 30;

		var ticketingId = createTicketingAndReturnId(mockEmail, mockStock, now.plusYears(1), now.plusYears(2),
			now.plusYears(3));
		var listTicketCommand = new ListTicketByTicketingCommandDto(ticketingId);

		// when
		var tickets = ticketService.listTicketByTicketing(listTicketCommand).getTickets();

		// then
		Assertions.assertThat(tickets.size()).isEqualTo(mockStock);
	}

	@Test
	@DisplayName("유효하지 않은 티케팅 > 티켓 생성 요청 > 실패")
	void createTicketsFailBecauseNotExistTicketing() {
		// given
		var invalidTicketingId = UUID.randomUUID();
		var command = CreateTicketCommandDto.builder().ticketingId(invalidTicketingId).numOfTickets(100).build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketService.createTickets(command);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("이미 판매 기간에 돌입한 티케팅 > 하위 티켓 생성 요청 > 실패")
	void createTicketsFailBecauseSaleDurationHasBeenStarted() {
		// given
		var now = LocalDateTime.now();

		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var mockStock = 10;
		var saleStart = now.plusYears(1);
		var ticketingId = createTicketingAndReturnId(mockEmail, mockStock, saleStart, now.plusYears(2),
			now.plusYears(3));
		var createTicketCommand = CreateTicketCommandDto.builder()
			.ticketingId(ticketingId)
			.numOfTickets(20)
			.commandCreatedAt(saleStart.plusDays(1))
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketService.createTickets(createTicketCommand);
			// then
		}).isInstanceOf(UpdateTicketingAfterSaleStartException.class);
	}

	@Test
	@DisplayName("유효한 티케팅 (기존 티켓 10) > 추가 하위 티켓 생성 요청 (20) > 성공 및 총 재고 10 + 20")
	void createTicketsSuccess() {
		// given
		var now = LocalDateTime.now();

		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var mockStock = 10;
		var ticketingId = createTicketingAndReturnId(mockEmail, mockStock, now.plusYears(1), now.plusYears(2),
			now.plusYears(3));

		var addTickets = 20;
		var createTicketCommand = CreateTicketCommandDto.builder()
			.ticketingId(ticketingId)
			.numOfTickets(addTickets)
			.build();

		// when
		ticketService.createTickets(createTicketCommand);

		// then
		var tickets = ticketService.listTicketByTicketing(new ListTicketByTicketingCommandDto(ticketingId));
		Assertions.assertThat(tickets.getTickets().size()).isEqualTo(mockStock + addTickets);
	}

	@Test
	@DisplayName("유효하지 않은 티케팅 > 티켓 삭제 요청 > 실패")
	void dropTicketsFailBecauseNotExistTicketing() {
		// given
		var invalidTicketingId = UUID.randomUUID();

		var command = DropNumOfTicketsUnderSomeTicketingCommandDto.builder()
			.ticketingId(invalidTicketingId)
			.numOfTickets(10)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketService.dropNumOfTicketsUnderSomeTicketing(command);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("이미 판매가 시작된 티케팅 > 티켓 삭제 요청 > 실패")
	void dropTicketsFailBecauseSaleDurationHasBeenStarted() {
		// given
		var now = LocalDateTime.now();

		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var mockStock = 30;
		var saleStart = now.plusYears(1);
		var ticketingId = createTicketingAndReturnId(mockEmail, mockStock, saleStart, now.plusYears(2),
			now.plusYears(3));

		var dropTicketCommand = DropNumOfTicketsUnderSomeTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.numOfTickets(20)
			.commandCreatedAt(saleStart.plusDays(1))
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketService.dropNumOfTicketsUnderSomeTicketing(dropTicketCommand);
			// then
		}).isInstanceOf(UpdateTicketingAfterSaleStartException.class);
	}

	@Test
	@DisplayName("유효한 티케팅 (기존 10) > 티켓 삭제 요청 (5) > 잔여 티켓 (5)")
	void dropTicketsSuccess() {
		// given
		var now = LocalDateTime.now();

		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var mockStock = 10;

		var ticketingId = createTicketingAndReturnId(mockEmail, mockStock, now.plusYears(1), now.plusYears(2),
			now.plusYears(3));

		var deleteTickets = 5;
		var dropTicketCommand = DropNumOfTicketsUnderSomeTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.numOfTickets(deleteTickets)
			.build();

		// when
		ticketService.dropNumOfTicketsUnderSomeTicketing(dropTicketCommand);

		// then
		var tickets = ticketService.listTicketByTicketing(new ListTicketByTicketingCommandDto(ticketingId));
		Assertions.assertThat(tickets.getTickets().size()).isEqualTo(mockStock - deleteTickets);
	}

	@Test
	@DisplayName("이미 판매가 시작된 티케팅 > 티켓 전체 삭제 요청 > 실패")
	void dropAllTicketsFailBecauseSaleDurationHasBeenStarted() {
		// given
		var now = LocalDateTime.now();

		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var mockStock = 30;
		var saleStart = now.plusYears(1);
		var ticketingId = createTicketingAndReturnId(mockEmail, mockStock, saleStart, now.plusYears(2),
			now.plusYears(3));

		var dropAllTicketCommand = DropAllTicketsUnderSomeTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.commandCreatedAt(saleStart.plusDays(1))
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketService.dropAllTicketsUnderSomeTicketing(dropAllTicketCommand);
			// then
		}).isInstanceOf(UpdateTicketingAfterSaleStartException.class);
	}

	@Test
	@DisplayName("유효한 티케팅 (기존 10) > 티켓 전체 삭제 요청 > 잔여 티켓 (0)")
	void dropAllTicketsSuccess() {
		// given
		var now = LocalDateTime.now();

		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var mockStock = 10;

		var ticketingId = createTicketingAndReturnId(mockEmail, mockStock, now.plusYears(1), now.plusYears(2),
			now.plusYears(3));

		var dropAllTicketCommand = DropAllTicketsUnderSomeTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.build();

		// when
		ticketService.dropAllTicketsUnderSomeTicketing(dropAllTicketCommand);

		// then
		var tickets = ticketService.listTicketByTicketing(new ListTicketByTicketingCommandDto(ticketingId));
		Assertions.assertThat(tickets.getTickets().size()).isEqualTo(0);
	}

	private void createMember(String email) {
		var role = roleRepository.findByName(RoleEnum.SELLER).orElseThrow();
		var memberForSave = Member.builder()
			.email(email)
			.password("1234456eqeqw").role(role).build();
		memberRepository.save(memberForSave);
	}

	private UUID createTicketingAndReturnId(String email, int stock, LocalDateTime saleStart, LocalDateTime saleEnd,
		LocalDateTime eventTime) {
		var createTicketingCommand = CreateTicketingCommandDto.builder()
			.memberEmail(email)
			.title("음악회")
			.location("서울 강남역 8번 출구")
			.category("음악회")
			.runningMinutes(100)
			.price(10000L)
			.stock(stock)
			.saleStart(saleStart)
			.saleEnd(saleEnd)
			.eventTime(eventTime)
			.build();

		return ticketingService.createTicketing(createTicketingCommand).getTicketingId();
	}
}
