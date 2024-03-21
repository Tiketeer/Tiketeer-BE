package com.tiketeer.Tiketeer.domain.ticketing.usecase;

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

import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.exception.EventTimeNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.ModifyForNotOwnedTicketingException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.UpdateTicketingAfterSaleStartException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.UpdateTicketingCommandDto;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
@DisplayName("UpdateTicketingUseCaseTest Test")
class UpdateTicketingUseCaseTest {

	@Autowired
	private TestHelper testHelper;
	@Autowired
	private UpdateTicketingUseCase updateTicketingUseCase;
	@Autowired
	private CreateTicketingUseCase createTicketingUseCase;
	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PurchaseRepository purchaseRepository;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("존재하지 않는 티케팅 > 티케팅 수정 요청 > 실패")
	void updateTicketingFailBecauseTicketingNotExist() {
		// given
		var invalidTicketingId = UUID.randomUUID();

		var updateTicketingCommand = UpdateTicketingCommandDto.builder().ticketingId(invalidTicketingId).build();

		Assertions.assertThatThrownBy(() -> {
			// when
			updateTicketingUseCase.updateTicketing(updateTicketingCommand);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("본인 소유가 아닌 티케팅 > 티케팅 수정 요청 > 실패")
	void updateTicketingFailBecauseNotOwnedTicketing() {
		// given
		var memberEmailOwnedTicketing = "test@test.com";
		testHelper.createMember(memberEmailOwnedTicketing);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var createCmd = createTicketingCommand(memberEmailOwnedTicketing, eventTime, saleStart, saleEnd);

		var ticketingId = createTicketingUseCase.createTicketing(createCmd).getTicketingId();

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
			updateTicketingUseCase.updateTicketing(updateTicketingCommand);
			// then
		}).isInstanceOf(ModifyForNotOwnedTicketingException.class);
	}

	@Test
	@DisplayName("이미 판매를 시작한 티케팅 > 티케팅 수정 요청 > 실패")
	void updateTicketingFailBecauseSaleDurationHasBeenStarted() {
		// given
		var mockEmail = "test@test.com";
		testHelper.createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var createCmd = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		var ticketingId = createTicketingUseCase.createTicketing(createCmd).getTicketingId();

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
			updateTicketingUseCase.updateTicketing(updateTicketingCommand);
			// then
		}).isInstanceOf(UpdateTicketingAfterSaleStartException.class);
	}

	@Test
	@DisplayName("수정될 이벤트 시점이 과거 시점 > 티케팅 수정 요청 > 실패")
	void updateTicketingFailBecauseInvalidEventTime() {
		// given
		var mockEmail = "test@test.com";
		testHelper.createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var createCmd = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		var ticketingId = createTicketingUseCase.createTicketing(createCmd).getTicketingId();

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
			updateTicketingUseCase.updateTicketing(updateTicketingCommand);
			// then
		}).isInstanceOf(EventTimeNotValidException.class);
	}

	@Test
	@DisplayName("유효하지 않은 판매 기간 (판매 시작 시점보다 판매 종료 시점이 빠름) > 티케팅 수정 요청 > 실패")
	void updateTicketingFailBecauseSaleDurationNotValid() {
		// given
		var mockEmail = "test@test.com";
		testHelper.createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var createCmd = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		var ticketingId = createTicketingUseCase.createTicketing(createCmd).getTicketingId();

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
			updateTicketingUseCase.updateTicketing(updateTicketingCommand);
			// then
		}).isInstanceOf(SaleDurationNotValidException.class);
	}

	@Test
	@DisplayName("유효하지 않은 판매 기간 (판매 기간 종료 전 이벤트가 시작함) > 티케팅 수정 요청 > 실패")
	void updateTicketingFailBecauseEventTimeBeforeSaleEnd() {
		// given
		var mockEmail = "test@test.com";
		testHelper.createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var createCmd = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		var ticketingId = createTicketingUseCase.createTicketing(createCmd).getTicketingId();

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
			updateTicketingUseCase.updateTicketing(updateTicketingCommand);
			// then
		}).isInstanceOf(SaleDurationNotValidException.class);
	}

	@Test
	@DisplayName("정상 수정 요청 > 티케팅 수정 > 성공")
	void updateTicketingSuccess() {
		// given
		var mockEmail = "test@test.com";
		testHelper.createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var createCmd = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		var ticketingId = createTicketingUseCase.createTicketing(createCmd).getTicketingId();

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
		updateTicketingUseCase.updateTicketing(updateTicketingCommand);

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