package com.tiketeer.Tiketeer.domain.ticketing.usecase;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.exception.EventTimeNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class CreateTicketingUseCaseTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private CreateTicketingUseCase createTicketingUseCase;
	@Autowired
	private TicketingRepository ticketingRepository;
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
	@DisplayName("이벤트 시점이 과거 시점 > 티켓팅 생성 요청 > 실패")
	void createTicketingFailBecauseInvalidEventTime() {
		// given
		var mockEmail = "test@test.com";
		testHelper.createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.minusYears(20);
		var command = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		Assertions.assertThatThrownBy(() -> {
			// when
			createTicketingUseCase.createTicketing(command);
			// then
		}).isInstanceOf(EventTimeNotValidException.class);
	}

	@Test
	@DisplayName("유효하지 않은 판매 기간 (판매 시작 시점보다 판매 종료 시점이 빠름) > 티켓팅 생성 요청 > 실패")
	void createTicketingFailBecauseInvalidSaleDuration() {
		// given
		var mockEmail = "test1@test.com";
		testHelper.createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(2);
		var saleEnd = now.plusYears(1);
		var eventTime = now.plusYears(3);
		var command = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		Assertions.assertThatThrownBy(() -> {
			// when
			createTicketingUseCase.createTicketing(command);
			// then
		}).isInstanceOf(SaleDurationNotValidException.class);
	}

	@Test
	@DisplayName("유효하지 않은 판매 기간 (판매 기간 종료 전 이벤트가 시작함) > 티켓팅 생성 요청 > 실패")
	void createTicketingFailBecauseEventTimeDuringSaleDuration() {
		// given
		var mockEmail = "test1@test.com";
		testHelper.createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(3);
		var eventTime = now.plusYears(2);
		var command = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		Assertions.assertThatThrownBy(() -> {
			// when
			createTicketingUseCase.createTicketing(command);
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
			createTicketingUseCase.createTicketing(command);
			// then
		}).isInstanceOf(MemberNotFoundException.class);
	}

	@Test
	@DisplayName("정상 컨디션 > 티켓팅 생성 요청 > 성공")
	void createTicketingSuccess() {
		// given
		var mockEmail = "test1@test.com";
		testHelper.createMember(mockEmail);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var command = createTicketingCommand(mockEmail, eventTime, saleStart, saleEnd);

		// when
		var result = createTicketingUseCase.createTicketing(command);

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
