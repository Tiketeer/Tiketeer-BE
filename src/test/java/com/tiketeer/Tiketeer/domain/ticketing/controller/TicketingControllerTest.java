package com.tiketeer.Tiketeer.domain.ticketing.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import com.tiketeer.Tiketeer.auth.constant.JwtMetadata;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.GetAllTicketingsResponseDto;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.GetTicketingResponseDto;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.PatchTicketingRequestDto;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.PostTicketingRequestDto;
import com.tiketeer.Tiketeer.domain.ticketing.controller.dto.PostTicketingResponseDto;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.CreateTicketingUseCase;
import com.tiketeer.Tiketeer.domain.ticketing.usecase.dto.CreateTicketingCommandDto;
import com.tiketeer.Tiketeer.response.ApiResponse;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

import jakarta.servlet.http.Cookie;

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
		var now = LocalDateTime.now();
		var email = "test@test.com";
		var accessToken = testHelper.registerAndLoginAndReturnAccessToken(email, RoleEnum.SELLER);
		var req = PostTicketingRequestDto.builder()
			.title("음악회")
			.description("설명")
			.location("서울 강남역 8번 출구")
			.category("음악회")
			.runningMinutes(100)
			.price(10000L)
			.stock(20)
			.eventTime(now.plusYears(3))
			.saleStart(now.plusYears(1))
			.saleEnd(now.plusYears(2))
			.build();

		// when
		mockMvc.perform(
				post("/api/ticketings")
					.contextPath("/api")
					.cookie(new Cookie(JwtMetadata.ACCESS_TOKEN, accessToken))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8)
					.content(objectMapper.writeValueAsString(req))
			).andExpect(status().is2xxSuccessful())
			.andDo(response -> {
				var result = testHelper.getDeserializedApiResponse(response.getResponse().getContentAsString(),
					PostTicketingResponseDto.class).getData();

				// then
				Assertions.assertThat(result.getTicketingId()).isNotNull();
				Assertions.assertThat(ticketingRepository.findById(result.getTicketingId()).isPresent()).isTrue();
			});
	}

	@Test
	@DisplayName(
		"이미 생성된 티케팅 존재 (제목: 음악회, 러닝타임: 100분, 재고:20)"
			+ " > 티케팅 수정 요청 (제목: 음악회1, 러닝타임: 120분, 재고:50) > 성공"
	)
	void patchTicketingSuccess() throws Exception {
		// given
		var email = "test@test.com";
		var accessToken = testHelper.registerAndLoginAndReturnAccessToken(email, RoleEnum.SELLER);

		var title = "음악회";
		var runningMinutes = 100;
		var stock = 20;
		var createTicketingCmd = createCreateTicketingCommand(email, title, runningMinutes, stock);
		var ticketingId = createTicketingUseCase.createTicketing(createTicketingCmd).getTicketingId();

		var updatedTitle = "음악회1";
		var updatedRunningMinutes = 120;
		var updatedStock = 50;
		var req = PatchTicketingRequestDto.builder()
			.title(updatedTitle)
			.description(createTicketingCmd.getDescription())
			.location(createTicketingCmd.getLocation())
			.category(createTicketingCmd.getCategory())
			.runningMinutes(updatedRunningMinutes)
			.price(createTicketingCmd.getPrice())
			.stock(updatedStock)
			.eventTime(createTicketingCmd.getEventTime())
			.saleStart(createTicketingCmd.getSaleStart())
			.saleEnd(createTicketingCmd.getSaleEnd())
			.build();

		// when
		mockMvc.perform(
			patch("/api/ticketings/" + ticketingId)
				.contextPath("/api")
				.cookie(new Cookie(JwtMetadata.ACCESS_TOKEN, accessToken))
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding(StandardCharsets.UTF_8)
				.content(objectMapper.writeValueAsString(req))
		).andExpect(status().is2xxSuccessful());

		// then
		var ticketingInfoList = ticketingRepository.findTicketingWithTicketStock(email);
		Assertions.assertThat(ticketingInfoList.size()).isEqualTo(1);

		var ticketingInfo = ticketingInfoList.getFirst();
		Assertions.assertThat(ticketingInfo.getTicketingId()).isEqualTo(ticketingId);
		Assertions.assertThat(ticketingInfo.getPrice()).isEqualTo(createTicketingCmd.getPrice());
		Assertions.assertThat(ticketingInfo.getCategory()).isEqualTo(createTicketingCmd.getCategory());
		Assertions.assertThat(ticketingInfo.getTitle()).isEqualTo(updatedTitle);
		Assertions.assertThat(ticketingInfo.getRemainStock()).isEqualTo(updatedStock);
		Assertions.assertThat(ticketingInfo.getRunningMinutes()).isEqualTo(updatedRunningMinutes);
	}

	@Test
	@DisplayName("이미 생성된 티케팅 존재 > 티케팅 삭제 요청 > 성공")
	void deleteTicketingSuccess() throws Exception {
		// given
		var email = "test@test.com";
		var accessToken = testHelper.registerAndLoginAndReturnAccessToken(email, RoleEnum.SELLER);

		var title = "음악회";
		var runningMinutes = 100;
		var stock = 20;
		var createTicketingCmd = createCreateTicketingCommand(email, title, runningMinutes, stock);
		var ticketingId = createTicketingUseCase.createTicketing(createTicketingCmd).getTicketingId();

		// when
		mockMvc.perform(
			delete("/api/ticketings/" + ticketingId)
				.contextPath("/api")
				.cookie(new Cookie(JwtMetadata.ACCESS_TOKEN, accessToken))
		).andExpect(status().is2xxSuccessful());

		// then
		var ticketingOpt = ticketingRepository.findById(ticketingId);
		Assertions.assertThat(ticketingOpt.isPresent()).isFalse();
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

	private CreateTicketingCommandDto createCreateTicketingCommand(String email, String title, int runningMinutes,
		int stock) {
		var now = LocalDateTime.now();
		return CreateTicketingCommandDto.builder()
			.memberEmail(email)
			.title(title)
			.price(1000L)
			.category("카테고리")
			.location("서울")
			.stock(stock)
			.runningMinutes(runningMinutes)
			.saleStart(now.plusYears(1))
			.saleEnd(now.plusYears(2))
			.eventTime(now.plusYears(3)).build();
	}
}
