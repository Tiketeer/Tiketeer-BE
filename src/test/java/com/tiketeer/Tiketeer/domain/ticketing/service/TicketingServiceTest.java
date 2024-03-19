package com.tiketeer.Tiketeer.domain.ticketing.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

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
import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.DeleteTicketingAfterSaleStartException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.EventTimeNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.ModifyForNotOwnedTicketingException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.UpdateTicketingAfterSaleStartException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.DeleteTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.UpdateTicketingCommandDto;
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
	@DisplayName("정상 조건 > 티켓팅 전체 조회 요청 > 성공")
	@Transactional
	void getAllTicketingsSuccess() {
		// given
		var mockEmail = "test@test.com";
		var member = createMember(mockEmail);
		var ticketCnt = 3;
		var ticketings = createTicketings(member, ticketCnt);

		// when
		var results = ticketingService.getAllTicketings();

		// then
		Assertions.assertThat(ticketings.size()).isEqualTo(ticketCnt);
		IntStream.range(0, ticketCnt).forEach(idx -> {
			Assertions.assertThat(results.get(idx).getTitle()).isEqualTo(idx + "");
		});

	}

	@Test
	@DisplayName("이벤트 시점이 과거 시점 > 티켓팅 생성 요청 > 실패")
	void createTicketingFailBecauseInvalidEventTime() {
		// given
		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.minusYears(20);
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
		var saleStart = now.plusYears(2);
		var saleEnd = now.plusYears(1);
		var eventTime = now.plusYears(3);
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
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(3);
		var eventTime = now.plusYears(2);
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
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
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
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
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
		Assertions.assertThat(ticketing.getEventTime()).isEqualToIgnoringNanos(command.getEventTime());
		Assertions.assertThat(ticketing.getSaleStart()).isEqualToIgnoringNanos(command.getSaleStart());
		Assertions.assertThat(ticketing.getSaleEnd()).isEqualToIgnoringNanos(command.getSaleEnd());

		var tickets = ticketRepository.findAllByTicketing(ticketing);
		Assertions.assertThat(tickets.size()).isEqualTo(command.getStock());
	}

	@Test
	@DisplayName("존재하지 않는 티케팅 > 티케팅 수정 요청 > 실패")
	void updateTicketingFailBecauseTicketingNotExist() {
		// given
		var invalidTicketingId = UUID.randomUUID();

		var updateTicketingCommand = UpdateTicketingCommandDto.builder().ticketingId(invalidTicketingId).build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.updateTicketing(updateTicketingCommand);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("본인 소유가 아닌 티케팅 > 티케팅 수정 요청 > 실패")
	void updateTicketingFailBecauseNotOwnedTicketing() {
		// given
		var memberEmailOwnedTicketing = "test@test.com";
		createMember(memberEmailOwnedTicketing);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var createCmd = createTicketingCommand(memberEmailOwnedTicketing, eventTime, saleStart, saleEnd);

		var ticketingId = ticketingService.createTicketing(createCmd).getTicketingId();

		var memberEmailNotOwnedTicketing = "another@test.com";
		var updateTicketingCommand = UpdateTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.email(memberEmailNotOwnedTicketing)
			.title(createCmd.getTitle())
			.price(createCmd.getPrice())
			.description(createCmd.getDescription())
			.category(createCmd.getCategory())
			.runningMinutes(createCmd.getRunningMinutes())
			.stock(createCmd.getStock())
			.saleStart(createCmd.getSaleStart())
			.saleEnd(createCmd.getSaleEnd())
			.eventTime(createCmd.getEventTime())
			.commandCreatedAt(saleStart.plusDays(1))
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.updateTicketing(updateTicketingCommand);
			// then
		}).isInstanceOf(ModifyForNotOwnedTicketingException.class);
	}

	@Test
	@DisplayName("이미 판매를 시작한 티케팅 > 티케팅 수정 요청 > 실패")
	void updateTicketingFailBecauseSaleDurationHasBeenStarted() {
		// given
		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var createCmd = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		var ticketingId = ticketingService.createTicketing(createCmd).getTicketingId();

		var updateTicketingCommand = UpdateTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.email(createCmd.getMemberEmail())
			.title(createCmd.getTitle())
			.price(createCmd.getPrice())
			.description(createCmd.getDescription())
			.category(createCmd.getCategory())
			.runningMinutes(createCmd.getRunningMinutes())
			.stock(createCmd.getStock())
			.saleStart(createCmd.getSaleStart())
			.saleEnd(createCmd.getSaleEnd())
			.eventTime(createCmd.getEventTime())
			.commandCreatedAt(saleStart.plusDays(1))
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.updateTicketing(updateTicketingCommand);
			// then
		}).isInstanceOf(UpdateTicketingAfterSaleStartException.class);
	}

	@Test
	@DisplayName("수정될 이벤트 시점이 과거 시점 > 티케팅 수정 요청 > 실패")
	void updateTicketingFailBecauseInvalidEventTime() {
		// given
		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var createCmd = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		var ticketingId = ticketingService.createTicketing(createCmd).getTicketingId();

		var updateTicketingCommand = UpdateTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.email(createCmd.getMemberEmail())
			.title(createCmd.getTitle())
			.price(createCmd.getPrice())
			.description(createCmd.getDescription())
			.category(createCmd.getCategory())
			.runningMinutes(createCmd.getRunningMinutes())
			.stock(createCmd.getStock())
			.saleStart(createCmd.getSaleStart())
			.saleEnd(createCmd.getSaleEnd())
			.eventTime(now.minusDays(1))
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.updateTicketing(updateTicketingCommand);
			// then
		}).isInstanceOf(EventTimeNotValidException.class);
	}

	@Test
	@DisplayName("유효하지 않은 판매 기간 (판매 시작 시점보다 판매 종료 시점이 빠름) > 티케팅 수정 요청 > 실패")
	void updateTicketingFailBecauseSaleDurationNotValid() {
		// given
		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var createCmd = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		var ticketingId = ticketingService.createTicketing(createCmd).getTicketingId();

		var updateTicketingCommand = UpdateTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.email(createCmd.getMemberEmail())
			.title(createCmd.getTitle())
			.price(createCmd.getPrice())
			.description(createCmd.getDescription())
			.category(createCmd.getCategory())
			.runningMinutes(createCmd.getRunningMinutes())
			.stock(createCmd.getStock())
			.saleStart(saleEnd)
			.saleEnd(saleStart)
			.eventTime(eventTime)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.updateTicketing(updateTicketingCommand);
			// then
		}).isInstanceOf(SaleDurationNotValidException.class);
	}

	@Test
	@DisplayName("유효하지 않은 판매 기간 (판매 기간 종료 전 이벤트가 시작함) > 티케팅 수정 요청 > 실패")
	void updateTicketingFailBecauseEventTimeBeforeSaleEnd() {
		// given
		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var createCmd = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		var ticketingId = ticketingService.createTicketing(createCmd).getTicketingId();

		var updateTicketingCommand = UpdateTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.email(createCmd.getMemberEmail())
			.title(createCmd.getTitle())
			.price(createCmd.getPrice())
			.description(createCmd.getDescription())
			.category(createCmd.getCategory())
			.runningMinutes(createCmd.getRunningMinutes())
			.stock(createCmd.getStock())
			.saleStart(saleStart)
			.saleEnd(saleEnd)
			.eventTime(saleEnd.minusDays(1))
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.updateTicketing(updateTicketingCommand);
			// then
		}).isInstanceOf(SaleDurationNotValidException.class);
	}

	@Test
	@DisplayName("정상 수정 요청 > 티케팅 수정 > 성공")
	void updateTicketingSuccess() {
		// given
		var mockEmail = "test@test.com";
		createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var createCmd = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		var ticketingId = ticketingService.createTicketing(createCmd).getTicketingId();

		var newTitle = "New Title!";
		var newDescription = "New!!!";
		var newPrice = createCmd.getPrice() * 2;
		var newCategory = createCmd.getCategory() + "!";
		var newLocation = createCmd.getLocation() + "!";
		var newRunningMinutes = createCmd.getRunningMinutes() * 2;
		var newStock = createCmd.getStock() + 10;
		var newSaleStart = createCmd.getSaleStart().plusMonths(1);
		var newSaleEnd = createCmd.getSaleEnd().plusMonths(1);
		var newEventTime = createCmd.getEventTime().plusMonths(1);

		var updateTicketingCommand = UpdateTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.email(createCmd.getMemberEmail())
			.title(newTitle)
			.price(newPrice)
			.location(newLocation)
			.description(newDescription)
			.category(newCategory)
			.runningMinutes(newRunningMinutes)
			.stock(newStock)
			.saleStart(newSaleStart)
			.saleEnd(newSaleEnd)
			.eventTime(newEventTime)
			.build();

		// when
		ticketingService.updateTicketing(updateTicketingCommand);

		// then
		var updatedTicketingOpt = ticketingRepository.findById(ticketingId);
		Assertions.assertThat(updatedTicketingOpt.isPresent()).isTrue();

		var updatedTicketing = updatedTicketingOpt.get();
		Assertions.assertThat(updatedTicketing.getTitle()).isEqualTo(newTitle);
		Assertions.assertThat(updatedTicketing.getDescription()).isEqualTo(newDescription);
		Assertions.assertThat(updatedTicketing.getCategory()).isEqualTo(newCategory);
		Assertions.assertThat(updatedTicketing.getLocation()).isEqualTo(newLocation);
		Assertions.assertThat(updatedTicketing.getRunningMinutes()).isEqualTo(newRunningMinutes);
		Assertions.assertThat(updatedTicketing.getPrice()).isEqualTo(newPrice);
		Assertions.assertThat(updatedTicketing.getEventTime()).isEqualToIgnoringNanos(newEventTime);
		Assertions.assertThat(updatedTicketing.getSaleStart()).isEqualToIgnoringNanos(newSaleStart);
		Assertions.assertThat(updatedTicketing.getSaleEnd()).isEqualToIgnoringNanos(newSaleEnd);

		var tickets = ticketRepository.findAllByTicketing(updatedTicketing);
		Assertions.assertThat(tickets.size()).isEqualTo(newStock);
	}

	@Test
	@DisplayName("존재하지 않는 티케팅 > 티케팅 삭제 요청 > 실패")
	void deleteTicketingFailBecauseTicketingNotExist() {
		// given
		var inValidTicketingId = UUID.randomUUID();

		var deleteTicketingCommand = DeleteTicketingCommandDto.builder().ticketingId(inValidTicketingId).build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.deleteTicketing(deleteTicketingCommand);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("본인 소유가 아닌 티케팅 > 티케팅 삭제 요청 > 실패")
	void deleteTicketingFailBecauseNotOwnedTicketing() {
		// given
		var emailOwnedTicketing = "test@test.com";
		createMember(emailOwnedTicketing);

		var now = LocalDateTime.now();
		var createTicketingCommand = createTicketingCommand(emailOwnedTicketing, now.plusYears(3), now.plusYears(1),
			now.plusYears(2));
		var ticketingId = ticketingService.createTicketing(createTicketingCommand).getTicketingId();

		var emailNotOwnedTicketing = "another@test.com";
		var deleteTicketingCommand = DeleteTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.memberEmail(emailNotOwnedTicketing)
			.commandCreatedAt(now)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.deleteTicketing(deleteTicketingCommand);
			// then
		}).isInstanceOf(ModifyForNotOwnedTicketingException.class);
	}

	@Test
	@DisplayName("판매를 시작한 티케팅 > 티케팅 삭제 요청 > 실패")
	void deleteTicketingFailBecauseSaleDurationHasBeenStarted() {
		// given
		var email = "test@test.com";
		createMember(email);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var createTicketingCommand = createTicketingCommand(email, now.plusYears(3), saleStart,
			now.plusYears(2));
		var ticketingId = ticketingService.createTicketing(createTicketingCommand).getTicketingId();

		var deleteTicketingCommand = DeleteTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.memberEmail(email)
			.commandCreatedAt(saleStart.plusDays(1))
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.deleteTicketing(deleteTicketingCommand);
			// then
		}).isInstanceOf(DeleteTicketingAfterSaleStartException.class);
	}

	@Test
	@DisplayName("삭제 가능한 조건의 티케팅 > 삭제 요청 > 삭제 성공 및 모든 하위 티켓 삭제")
	@Transactional
	void deleteTicketingSuccess() {
		// given
		var email = "test@test.com";
		createMember(email);

		var now = LocalDateTime.now();
		var createTicketingCommand = createTicketingCommand(email, now.plusYears(3), now.plusYears(1),
			now.plusYears(2));
		var ticketingId = ticketingService.createTicketing(createTicketingCommand).getTicketingId();

		var ticketingOpt = ticketingRepository.findById(ticketingId);
		Assertions.assertThat(ticketingOpt.isPresent()).isTrue();

		var ticketing = ticketingOpt.get();
		Assertions.assertThat(ticketRepository.findAllByTicketing(ticketing).size())
			.isEqualTo(createTicketingCommand.getStock());

		var deleteTicketingCommand = DeleteTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.memberEmail(email)
			.commandCreatedAt(now)
			.build();

		// when
		ticketingService.deleteTicketing(deleteTicketingCommand);

		// then
		var ticketsUnderTicketing = ticketRepository.findAllByTicketing(ticketing);
		Assertions.assertThat(ticketsUnderTicketing.size()).isEqualTo(0);

		var ticketingInDBOpt = ticketingRepository.findById(ticketingId);
		Assertions.assertThat(ticketingInDBOpt.isPresent()).isFalse();

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

	private List<Ticketing> createTicketings(Member member, int count) {
		List<String> titles = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			titles.add(i + "");
		}
		var ticketings = titles.stream().map(title ->
			Ticketing.builder()
				.member(member)
				.title(title)
				.location("서울")
				.category("콘서트")
				.runningMinutes(100)
				.price(10000)
				.eventTime(LocalDateTime.now().plusMonths(2))
				.saleStart(LocalDateTime.now().minusMonths(1))
				.saleEnd(LocalDateTime.now().plusMonths(1))
				.build()
		).toList();

		return ticketingRepository.saveAll(ticketings);
	}
}
