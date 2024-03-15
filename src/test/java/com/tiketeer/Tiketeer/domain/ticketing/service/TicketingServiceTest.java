package com.tiketeer.Tiketeer.domain.ticketing.service;

import java.time.LocalDateTime;

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
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.exception.EventTimeNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
@DisplayName("TicketingService Test")
public class TicketingServiceTest {
	private final TestHelper testHelper;
	private final TicketingService ticketingService;
	private final TicketingRepository ticketingRepository;
	private final TicketRepository ticketRepository;
	private final MemberRepository memberRepository;
	private final RoleRepository roleRepository;

	@Autowired
	public TicketingServiceTest(
		TestHelper testHelper,
		TicketingService ticketingService,
		TicketingRepository ticketingRepository,
		TicketRepository ticketRepository,
		MemberRepository memberRepository,
		RoleRepository roleRepository
	) {
		this.testHelper = testHelper;
		this.ticketingService = ticketingService;
		this.ticketingRepository = ticketingRepository;
		this.ticketRepository = ticketRepository;
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
	@DisplayName("이벤트 시점이 과거 시점 > 티켓팅 생성 요청 > 실패")
	void createTicketingFailBecauseInvalidEventTime() {
		// given
		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var now = LocalDateTime.now();
		var eventTime = LocalDateTime.of(1970, 1, 1, 12, 0);
		var saleStart = LocalDateTime.of(now.getYear() + 1, 1, 1, 0, 0);
		var saleEnd = LocalDateTime.of(now.getYear() + 2, 1, 1, 0, 0);
		var command = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.createTicketing(command);
			// then
		}).isInstanceOf(EventTimeNotValidException.class);
	}

	@Test
	@DisplayName("유효하지 않은 판매 기간 (판매 시작 시점보다 판매 종료 시점이 빠름) > 티켓팅 생성 요청 > 실패")
	void createTicketingFailBecauseInvalidSaleDuration() {
		// given
		var mockEmail = "test1@test.com";
		createMember(mockEmail);

		var now = LocalDateTime.now();
		var eventTime = LocalDateTime.of(now.getYear() + 3, 1, 1, 12, 0);
		var saleStart = LocalDateTime.of(now.getYear() + 2, 1, 1, 0, 0);
		var saleEnd = LocalDateTime.of(now.getYear() + 1, 1, 1, 0, 0);
		var command = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.createTicketing(command);
			// then
		}).isInstanceOf(SaleDurationNotValidException.class);
	}

	@Test
	@DisplayName("유효하지 않은 판매 기간 (판매 기간 종료 전 이벤트가 시작함) > 티켓팅 생성 요청 > 실패")
	void createTicketingFailBecauseEventTimeDuringSaleDuration() {
		// given
		var mockEmail = "test1@test.com";
		createMember(mockEmail);

		var now = LocalDateTime.now();
		var eventTime = LocalDateTime.of(now.getYear() + 2, 1, 1, 12, 0);
		var saleStart = LocalDateTime.of(now.getYear() + 1, 1, 1, 0, 0);
		var saleEnd = LocalDateTime.of(now.getYear() + 3, 1, 1, 0, 0);
		var command = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.createTicketing(command);
			// then
		}).isInstanceOf(SaleDurationNotValidException.class);
	}

	@Test
	@DisplayName("존재하지 않는 이메일(멤버) > 티켓팅 생성 요청 > 실패")
	void createTicketingFailBecauseInvalidEmail() {
		// given
		var mockEmail = "test1@test.com";

		var now = LocalDateTime.now();
		var eventTime = LocalDateTime.of(now.getYear() + 3, 1, 1, 12, 0);
		var saleStart = LocalDateTime.of(now.getYear() + 1, 1, 1, 0, 0);
		var saleEnd = LocalDateTime.of(now.getYear() + 2, 1, 1, 0, 0);
		var command = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.createTicketing(command);
			// then
		}).isInstanceOf(MemberNotFoundException.class);
	}

	@Test
	@DisplayName("정상 컨디션 > 티켓팅 생성 요청 > 성공")
	void createTicketingSuccess() {
		// given
		var mockEmail = "test1@test.com";
		createMember(mockEmail);

		var now = LocalDateTime.now();
		var eventTime = LocalDateTime.of(now.getYear() + 3, 1, 1, 12, 0);
		var saleStart = LocalDateTime.of(now.getYear() + 1, 1, 1, 0, 0);
		var saleEnd = LocalDateTime.of(now.getYear() + 2, 1, 1, 0, 0);
		var command = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		// when
		var result = ticketingService.createTicketing(command);

		// then
		var ticketingOpt = ticketingRepository.findById(result.getTicketingId());
		Assertions.assertThat(ticketingOpt.isPresent()).isTrue();

		var ticketing = ticketingOpt.get();
		Assertions.assertThat(ticketing.getTitle()).isEqualTo(command.getTitle());
		Assertions.assertThat(ticketing.getDescription()).isEqualTo(command.getDescription());
		Assertions.assertThat(ticketing.getCategory()).isEqualTo(command.getCategory());
		Assertions.assertThat(ticketing.getEventTime()).isEqualTo(command.getEventTime());
		Assertions.assertThat(ticketing.getSaleStart()).isEqualTo(command.getSaleStart());
		Assertions.assertThat(ticketing.getSaleEnd()).isEqualTo(command.getSaleEnd());

		var tickets = ticketRepository.findAllByTicketing(ticketing);
		Assertions.assertThat(tickets.size()).isEqualTo(command.getStock());
	}

	private Member createMember(String email) {
		var role = roleRepository.findByName(RoleEnum.SELLER).orElseThrow();
		var memberForSave = Member.builder()
			.email(email)
			.password("1234456eqeqw").role(role).build();
		return memberRepository.save(memberForSave);
	}

	private CreateTicketingCommandDto createTicketingCommand(String email, LocalDateTime eventTime,
		LocalDateTime saleStart, LocalDateTime saleEnd) {
		return CreateTicketingCommandDto.builder()
			.memberEmail(email)
			.title("음악회")
			.location("서울 강남역 8번 출구")
			.category("음악회")
			.runningMinutes(100)
			.price(10000L)
			.stock(20)
			.eventTime(eventTime)
			.saleStart(saleStart)
			.saleEnd(saleEnd)
			.build();
	}
}
