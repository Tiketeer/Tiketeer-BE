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

import com.tiketeer.Tiketeer.domain.ticketing.exception.ModifyForNotOwnedTicketingException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.DeleteTicketingCommandDto;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
@DisplayName("DeleteTicketingUseCaseTest Test")
class DeleteTicketingUseCaseTest {

	@Autowired
	private TestHelper testHelper;
	@Autowired
	private DeleteTicketingUseCase deleteTicketingUseCase;
	@Autowired
	private CreateTicketingUseCase createTicketingUseCase;

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
			deleteTicketingUseCase.deleteTicketing(deleteTicketingCommand);
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
		var ticketingId = createTicketingUseCase.createTicketing(createTicketingCommand).getTicketingId();

		var emailNotOwnedTicketing = "another@test.com";
		var deleteTicketingCommand = DeleteTicketingCommandDto.builder()
			.ticketingId(ticketingId)
			.memberEmail(emailNotOwnedTicketing)
			.commandCreatedAt(now)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			deleteTicketingUseCase.deleteTicketing(deleteTicketingCommand);
			// then
		}).isInstanceOf(ModifyForNotOwnedTicketingException.class);
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