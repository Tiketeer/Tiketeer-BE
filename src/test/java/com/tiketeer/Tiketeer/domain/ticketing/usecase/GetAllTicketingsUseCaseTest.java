package com.tiketeer.Tiketeer.domain.ticketing.usecase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
@DisplayName("GetAllTicketingsUseCaseTest Test")
class GetAllTicketingsUseCaseTest {

	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private GetAllTicketingsUseCase getAllTicketingsUseCase;

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
		var results = getAllTicketingsUseCase.getAllTicketings();

		// then
		Assertions.assertThat(ticketings.size()).isEqualTo(ticketCnt);
		IntStream.range(0, ticketCnt).forEach(idx -> {
			Assertions.assertThat(results.get(idx).getTitle()).isEqualTo(idx + "");
		});

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