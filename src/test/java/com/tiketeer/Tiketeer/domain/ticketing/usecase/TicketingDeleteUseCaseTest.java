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
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.exception.DeleteTicketingAfterSaleStartException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.ModifyForNotOwnedTicketingException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.service.dto.DeleteTicketingCommandDto;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class TicketingDeleteUseCaseTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private TicketingDeleteUseCase ticketingDeleteUseCase;
	@Autowired
	private TicketingCreateUseCase ticketingCreateUseCase;
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
	@DisplayName("존재하지 않는 티케팅 > 티케팅 삭제 요청 > 실패")
	void deleteTicketingFailBecauseTicketingNotExist() {
		// given
		var inValidTicketingId = UUID.randomUUID();

		var deleteTicketingCommand = DeleteTicketingCommandDto.builder().ticketingId(inValidTicketingId).build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingDeleteUseCase.deleteTicketing(deleteTicketingCommand);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("본인 소유가 아닌 티케팅 > 티케팅 삭제 요청 > 실패")
	void deleteTicketingFailBecauseNotOwnedTicketing() {
		// given
		var emailOwnedTicketing = "test@test.com";
		testHelper.createMember(emailOwnedTicketing);

		var now = LocalDateTime.now();
		var createTicketingCommand = createTicketingCommand(emailOwnedTicketing, now.plusYears(3), now.plusYears(1),
			now.plusYears(2));
		var ticketingId = ticketingCreateUseCase.createTicketing(createTicketingCommand).getTicketingId();

		var emailNotOwnedTicketing = "another@test.com";
		var deleteTicketingCommand = DeleteTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.memberEmail(emailNotOwnedTicketing)
			.commandCreatedAt(now)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingDeleteUseCase.deleteTicketing(deleteTicketingCommand);
			// then
		}).isInstanceOf(ModifyForNotOwnedTicketingException.class);
	}

	@Test
	@DisplayName("판매를 시작한 티케팅 > 티케팅 삭제 요청 > 실패")
	void deleteTicketingFailBecauseSaleDurationHasBeenStarted() {
		// given
		var email = "test@test.com";
		testHelper.createMember(email);

		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var createTicketingCommand = createTicketingCommand(email, now.plusYears(3), saleStart,
			now.plusYears(2));
		var ticketingId = ticketingCreateUseCase.createTicketing(createTicketingCommand).getTicketingId();

		var deleteTicketingCommand = DeleteTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.memberEmail(email)
			.commandCreatedAt(saleStart.plusDays(1))
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingDeleteUseCase.deleteTicketing(deleteTicketingCommand);
			// then
		}).isInstanceOf(DeleteTicketingAfterSaleStartException.class);
	}

	@Test
	@DisplayName("삭제 가능한 조건의 티케팅 > 삭제 요청 > 삭제 성공 및 모든 하위 티켓 삭제")
	@Transactional
	void deleteTicketingSuccess() {
		// given
		var email = "test@test.com";
		testHelper.createMember(email);

		var now = LocalDateTime.now();
		var createTicketingCommand = createTicketingCommand(email, now.plusYears(3), now.plusYears(1),
			now.plusYears(2));
		var ticketingId = ticketingCreateUseCase.createTicketing(createTicketingCommand).getTicketingId();

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
		ticketingDeleteUseCase.deleteTicketing(deleteTicketingCommand);

		// then
		var ticketsUnderTicketing = ticketRepository.findAllByTicketing(ticketing);
		Assertions.assertThat(ticketsUnderTicketing.size()).isEqualTo(0);

		var ticketingInDBOpt = ticketingRepository.findById(ticketingId);
		Assertions.assertThat(ticketingInDBOpt.isPresent()).isFalse();

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
