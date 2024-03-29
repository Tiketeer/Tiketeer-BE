package com.tiketeer.Tiketeer.domain.ticketing.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.data.domain.Limit;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.exception.SaleDurationNotValidException;
import com.tiketeer.Tiketeer.domain.ticketing.exception.TicketingNotFoundException;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.GetTicketingCommandDto;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
@DisplayName("TicketingService Test")
public class TicketingServiceTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private TicketingService ticketingService;
	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private TicketRepository ticketRepository;
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
	@DisplayName("정상 조건 > 티켓팅 전체 조회 요청 > 성공")
	@Transactional
	void getAllTicketingsSuccess() {
		// given
		var mockEmail = "test@test.com";
		var member = testHelper.createMember(mockEmail);
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
	@DisplayName("정상 조건 > 특정 티켓팅 조회 요청 > 성공")
	@Transactional
	void getTicketingSuccess() {
		// given
		var mockEmail = "test@test.com";
		var member = testHelper.createMember(mockEmail);
		var ticketings = createTicketings(member, 1);
		var ticketing = ticketings.getFirst();
		var stock = 10;
		var purchasedStock = 2;
		createTickets(ticketing, stock);
		createPurchase(member, ticketing, purchasedStock);

		var command = GetTicketingCommandDto.builder().ticketingId(ticketing.getId()).build();

		// when
		var result = ticketingService.getTickting(command);

		// then
		Assertions.assertThat(result.getTitle()).isEqualTo("0");
		Assertions.assertThat(result.getStock()).isEqualTo(stock);
		Assertions.assertThat(result.getRemainedStock()).isEqualTo(stock - purchasedStock);
		Assertions.assertThat(result.getOwner()).isEqualTo(member.getEmail());
	}

	@Test
	@DisplayName("존재하지 않는 티케팅 > 조회 요청 > 실패")
	void findByIdFailBecauseNotExistTicketing() {
		// given
		var invalidTicketingId = UUID.randomUUID();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.findById(invalidTicketingId);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("존재하는 티케팅 > 조회 요청 > 성공")
	@Transactional
	void findByIdSuccess() {
		// given
		var member = testHelper.createMember("test@test.com");
		var now = LocalDateTime.now();
		var ticketing = ticketingService.saveTicketing(Ticketing.builder()
			.member(member)
			.title("tt")
			.price(100L)
			.runningMinutes(100)
			.category("카테고리")
			.location("서울 어딘가")
			.saleStart(now.plusYears(1))
			.saleEnd(now.plusYears(2))
			.eventTime(now.plusYears(3))
			.build());

		// when
		var ticketingInDB = ticketingService.findById(ticketing.getId());

		// then
		Assertions.assertThat(ticketingInDB.getId()).isEqualTo(ticketing.getId());
	}

	@Test
	@DisplayName("유효하지 않은 판매 시작, 종료 일 > 티케팅 생성 요청 > 실패")
	@Transactional
	void saveTicketingFailBecauseSaleStartAndEndNotValid() {
		// given
		var member = testHelper.createMember("test@test.com");
		var now = LocalDateTime.now();
		var saleStart = now.plusYears(2);
		var saleEnd = now.plusYears(1);
		var eventTime = now.plusYears(3);
		var ticketingBeforeSave = Ticketing.builder()
			.member(member)
			.title("tt")
			.price(100L)
			.runningMinutes(100)
			.category("카테고리")
			.location("서울 어딘가")
			.saleStart(saleStart)
			.saleEnd(saleEnd)
			.eventTime(eventTime)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.saveTicketing(ticketingBeforeSave);
			// then
		}).isInstanceOf(SaleDurationNotValidException.class);
	}

	@Test
	@DisplayName("유효하지 않은 판매 종료, 이벤트 시작 일시 > 티케팅 생성 요청 > 실패")
	@Transactional
	void saveTicketingFailBecauseSaleEndAndEventTimeNotValid() {
		// given
		var member = testHelper.createMember("test@test.com");
		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(3);
		var eventTime = now.plusYears(2);
		var ticketingBeforeSave = Ticketing.builder()
			.member(member)
			.title("tt")
			.price(100L)
			.runningMinutes(100)
			.category("카테고리")
			.location("서울 어딘가")
			.saleStart(saleStart)
			.saleEnd(saleEnd)
			.eventTime(eventTime)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.saveTicketing(ticketingBeforeSave);
			// then
		}).isInstanceOf(SaleDurationNotValidException.class);
	}

	@Test
	@DisplayName("유효한 티케팅 메타데이터 > 티케팅 생성 요청 > 성공")
	@Transactional
	void saveTicketingSuccess() {
		// given
		var member = testHelper.createMember("test@test.com");
		var now = LocalDateTime.now();
		var saleStart = now.plusYears(1);
		var saleEnd = now.plusYears(2);
		var eventTime = now.plusYears(3);
		var ticketingBeforeSave = Ticketing.builder()
			.member(member)
			.title("tt")
			.price(100L)
			.runningMinutes(100)
			.category("카테고리")
			.location("서울 어딘가")
			.saleStart(saleStart)
			.saleEnd(saleEnd)
			.eventTime(eventTime)
			.build();

		// when
		var ticketing = ticketingService.saveTicketing(ticketingBeforeSave);

		// then
		var ticketingOpt = ticketingRepository.findById(ticketing.getId());
		Assertions.assertThat(ticketingOpt.isPresent()).isTrue();
	}

	@Test
	@DisplayName("존재하지 않는 티케팅 > 티케팅 삭제 요청 > 실패")
	void deleteTicketingFailBecauseNotExistTicketing() {
		// given
		var ticketingId = UUID.randomUUID();

		Assertions.assertThatThrownBy(() -> {
			// when
			ticketingService.deleteTicketing(ticketingId);
			// then
		}).isInstanceOf(TicketingNotFoundException.class);
	}

	@Test
	@DisplayName("존재하는 티케팅 > 삭제 요청 > 성공")
	void deleteTicketingSuccess() {
		// given
		var member = testHelper.createMember("test@test.com");
		var now = LocalDateTime.now();
		var ticketing = ticketingService.saveTicketing(Ticketing.builder()
			.member(member)
			.title("tt")
			.price(100L)
			.runningMinutes(100)
			.category("카테고리")
			.location("서울 어딘가")
			.saleStart(now.plusYears(1))
			.saleEnd(now.plusYears(2))
			.eventTime(now.plusYears(3))
			.build());

		Assertions.assertThat(ticketingService.findById(ticketing.getId()).getId()).isNotNull();

		// when
		ticketingService.deleteTicketing(ticketing.getId());

		// then
		Assertions.assertThatThrownBy(() -> {
			ticketingService.findById(ticketing.getId());
		}).isInstanceOf(TicketingNotFoundException.class);
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

	private List<Ticket> createTickets(Ticketing ticketing, int stock) {
		return ticketRepository.saveAll(Arrays.stream(new int[stock])
			.mapToObj(i -> Ticket.builder().ticketing(ticketing).build())
			.toList());
	}

	private Purchase createPurchase(Member member, Ticketing ticketing, int count) {
		var purchase = purchaseRepository.save(Purchase.builder().member(member).build());
		var tickets = ticketRepository.findByTicketingIdAndPurchaseIsNullOrderById(ticketing.getId(), Limit.of(count));
		tickets.forEach(ticket -> ticket.setPurchase(purchase));
		return purchase;
	}
}
