package com.tiketeer.Tiketeer.domain.ticketing.service;

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

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class TicketingStockServiceTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private TicketingStockService ticketingStockService;
	@Autowired
	private TicketingService ticketingService;
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
	@DisplayName("존재하지 않는 티케팅 > 하위 재고(티켓) 생성 요청 > 실패")
	void createStockFailBecauseNotExistTicketing() {
		// given
		var invalidTicketingId = UUID.randomUUID();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingStockService.createStock(invalidTicketingId, 100);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("정상 컨디션 (재고: 0) > 재고(티켓) 생성 요청 (20) > 티켓의 수 20")
	@Transactional
	void createStockSuccess() {
		// given
		var email = "test@test.com";
		var member = testHelper.createMember(email);

		var ticketing = createTicketing(member);
		var addStock = 30;

		Assertions.assertThat(ticketRepository.findAllByTicketing(ticketing)).isEmpty();

		// when
		ticketingStockService.createStock(ticketing.getId(), addStock);

		// then
		var tickets = ticketRepository.findAllByTicketing(ticketing);
		Assertions.assertThat(tickets.size()).isEqualTo(addStock);
	}

	@Test
	@DisplayName("존재하지 않는 티케팅 > 재고 수정 요청 > 실패")
	void updateStockFailBecauseNotExistTicketing() {
		// given
		var invalidTicketingId = UUID.randomUUID();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingStockService.updateStock(invalidTicketingId, 10);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("기존 재고의 수(10) > 재고 수정 요청(20) > 재고 업데이트 성공 (10 -> 20)")
	@Transactional
	void updateStockSuccessForAddRequest() {
		// given
		var email = "test@test.com";
		var member = testHelper.createMember(email);

		var ticketing = createTicketing(member);
		var initStock = 10;
		ticketingStockService.createStock(ticketing.getId(), initStock);

		Assertions.assertThat(ticketRepository.findAllByTicketing(ticketing).size()).isEqualTo(initStock);

		// when
		var updateStock = 20;
		ticketingStockService.updateStock(ticketing.getId(), updateStock);

		// then
		var tickets = ticketRepository.findAllByTicketing(ticketing);
		Assertions.assertThat(tickets.size()).isEqualTo(updateStock);
	}

	@Test
	@DisplayName("기존 재고의 수(30) > 재고 수정 요청(10) > 재고 업데이트 성공 (30 -> 10)")
	@Transactional
	void updateStockSuccessForRemoveRequest() {
		// given
		var email = "test@test.com";
		var member = testHelper.createMember(email);

		var ticketing = createTicketing(member);
		var initStock = 30;
		ticketingStockService.createStock(ticketing.getId(), initStock);

		Assertions.assertThat(ticketRepository.findAllByTicketing(ticketing).size()).isEqualTo(initStock);

		// when
		var updateStock = 10;
		ticketingStockService.updateStock(ticketing.getId(), updateStock);

		// then
		var tickets = ticketRepository.findAllByTicketing(ticketing);
		Assertions.assertThat(tickets.size()).isEqualTo(updateStock);
	}

	@Test
	@DisplayName("존재하지 않는 티케팅 > 재고 전체 삭제 요청 > 실패")
	void dropAllStockFailBecauseNotExistTicketing() {
		// given
		var invalidTicketingId = UUID.randomUUID();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingStockService.dropAllStock(invalidTicketingId);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("기존 재고의 수(30) > 재고 전체 삭제 요청 > 재고 삭제 성공 (30 -> 0)")
	@Transactional
	void dropAllStockSuccess() {
		// given
		var email = "test@test.com";
		var member = testHelper.createMember(email);

		var ticketing = createTicketing(member);
		var initStock = 30;
		ticketingStockService.createStock(ticketing.getId(), initStock);

		Assertions.assertThat(ticketRepository.findAllByTicketing(ticketing).size()).isEqualTo(initStock);

		// when
		ticketingStockService.dropAllStock(ticketing.getId());

		// then
		var tickets = ticketRepository.findAllByTicketing(ticketing);
		Assertions.assertThat(tickets.size()).isEqualTo(0);
	}

	private Ticketing createTicketing(Member member) {
		var now = LocalDateTime.now();
		return ticketingRepository.save(Ticketing.builder()
			.member(member)
			.title("음악회")
			.location("서울 강남역 8번 출구")
			.category("음악회")
			.runningMinutes(100)
			.price(10000L)
			.saleStart(now.plusYears(1))
			.saleEnd(now.plusYears(2))
			.eventTime(now.plusYears(3)).build());
	}
}
