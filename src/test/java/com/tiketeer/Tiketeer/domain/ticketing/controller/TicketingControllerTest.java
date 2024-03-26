package com.tiketeer.Tiketeer.domain.ticketing.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.GetAllTicketingsResponseDto;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.GetTicketingResponseDto;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.CreateTicketingUseCase;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.response.ApiResponse;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
@AutoConfigureMockMvc
public class TicketingControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private ObjectMapper objectMapper;
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
	@DisplayName("정상 조건 > 티켓팅 전체 조회 요청 > 성공")
	@Transactional
	void getAllTicketingsSuccess() throws Exception {
		// given
		var member = testHelper.createMember("user@example.com", RoleEnum.SELLER);
		var ticketCnt = 3;
		createTicketings(member, ticketCnt);

		// when
		var result = mockMvc.perform(get("/api/ticketings")
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
			)
			//then
			.andExpect(status().isOk());

		ApiResponse<List<GetAllTicketingsResponseDto>> apiResponse = testHelper.getDeserializedListApiResponse(
			result.andReturn().getResponse().getContentAsString(), GetAllTicketingsResponseDto.class);

		var ticketings = apiResponse.getData();
		// then
		Assertions.assertThat(ticketings.size()).isEqualTo(ticketCnt);
		IntStream.range(0, ticketCnt).forEach(idx -> {
			Assertions.assertThat(ticketings.get(idx).getTitle()).isEqualTo(idx + "");
		});
	}

	@Test
	@DisplayName("정상 조건 > 특정 티켓팅 조회 요청 > 성공")
	@Transactional
	void getTicketingSuccess() throws Exception {
		// given
		var member = testHelper.createMember("user@example.com", RoleEnum.SELLER);
		var ticketingInDb = createTicketings(member, 1).getFirst();
		var stock = 10;
		createTickets(ticketingInDb, stock);

		// when
		var result = mockMvc.perform(get("/api/ticketings/{ticketingId}", ticketingInDb.getId())
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
			)
			//then
			.andExpect(status().isOk());

		ApiResponse<GetTicketingResponseDto> apiResponse = testHelper.getDeserializedApiResponse(
			result.andReturn().getResponse().getContentAsString(), GetTicketingResponseDto.class);
		var ticketing = apiResponse.getData();
		Assertions.assertThat(ticketing.getTitle()).isEqualTo("0");
		Assertions.assertThat(ticketing.getStock()).isEqualTo(stock);
		Assertions.assertThat(ticketing.getRemainedStock()).isEqualTo(stock);
		Assertions.assertThat(ticketing.getOwner()).isEqualTo(member.getEmail());
	}

	@Test
	@DisplayName("정상 컨디션 > 티케팅 생성 요청 > 성공")
	void postTicketingSuccess() throws Exception {
		// given
		var email = "test@test.com";
		var accessToken = testHelper.registerAndLoginAndReturnAccessToken(email, RoleEnum.SELLER);
		// when
		// then
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

	private CreateTicketingCommandDto createCreateTicketingCommand(String email) {
		var now = LocalDateTime.now();
		return CreateTicketingCommandDto.builder()
			.memberEmail(email)
			.title("타이틀" + UUID.randomUUID())
			.price(1000L)
			.category("카테고리")
			.location("서울")
			.stock(5)
			.runningMinutes(100)
			.saleStart(now.plusYears(1))
			.saleEnd(now.plusYears(2))
			.eventTime(now.plusYears(3)).build();
	}
}
