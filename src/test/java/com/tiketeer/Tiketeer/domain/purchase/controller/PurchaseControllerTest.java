package com.tiketeer.Tiketeer.domain.purchase.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Limit;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiketeer.Tiketeer.auth.constant.JwtMetadata;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.controller.dto.DeletePurchaseTicketsRequestDto;
import com.tiketeer.Tiketeer.domain.purchase.controller.dto.PostPurchaseRequestDto;
import com.tiketeer.Tiketeer.domain.purchase.exception.NotEnoughTicketException;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

import jakarta.servlet.http.Cookie;

@Import({TestHelper.class})
@SpringBootTest
@AutoConfigureMockMvc
public class PurchaseControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	void initDB() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanDB() {
		testHelper.cleanDB();
	}

	@Test
	@Transactional
	@DisplayName("정상 조건 > 구매 생성 요청 > 성공")
	void postPurchaseSuccess() throws Exception {
		// given
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.SELLER);
		Member member = memberRepository.findAll().getFirst();
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);
		var ticketing = createTicketing(member, 0, 2);

		var count = 2;
		PostPurchaseRequestDto req = PostPurchaseRequestDto.builder()
			.ticketingId(ticketing.getId())
			.count(count)
			.build();
		String content = objectMapper.writeValueAsString(req);

		// when
		mockMvc
			.perform(post("/api/purchases")
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(content)
				.cookie(cookie)
			)
			//then
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.purchaseId").value(purchaseRepository.findAll().getFirst().getId().toString()
			));
		// System.out.println("print response content");
		// System.out.println(result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8));
		// ApiResponse response = objectMapper.readValue(
		// 	result.andReturn().getResponse().getContentAsString(),
		// 	ApiResponse.class);
		// PostPurchaseResponseDto dto = objectMapper.convertValue(response.getData(), PostPurchaseResponseDto.class);
		// System.out.println("print PostPurchaseResponseDto");
		// var purchases = purchaseRepository.findAll();
		//
		// //then
		// Assertions.assertThat(purchases.size()).isEqualTo(1);
		// result
		// 	.andExpect(status().isCreated())
		// 	.andExpect(jsonPath("$.data.purchaseId").value(purchases.getFirst().getId().toString()
		// 	));
	}

	@Test
	@Transactional
	@DisplayName("티케팅 판매 기간이 아님 > 구매 생성 요청 > 실패")
	void postPurchaseFailNotInSalePeriod() throws Exception {
		// given
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.SELLER);
		Member member = memberRepository.findAll().getFirst();
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);
		var ticketing = createTicketing(member, 1, 2);

		var count = 2;
		PostPurchaseRequestDto req = PostPurchaseRequestDto.builder()
			.ticketingId(ticketing.getId())
			.count(count)
			.build();
		String content = objectMapper.writeValueAsString(req);

		// when
		mockMvc
			.perform(post("/api/purchases")
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(content)
				.cookie(cookie)
			)
			//then
			.andExpect(status().isBadRequest());
	}

	@Test
	@Transactional
	@DisplayName("구매 가능한 티켓이 부족 > 구매 생성 요청 > 실패")
	void postPurchaseFailNotEnoughTicket() throws Exception {
		// given
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.SELLER);
		Member member = memberRepository.findAll().getFirst();
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);
		var ticketing = createTicketing(member, 0, 2);

		var count = 3;
		PostPurchaseRequestDto req = PostPurchaseRequestDto.builder()
			.ticketingId(ticketing.getId())
			.count(count)
			.build();
		String content = objectMapper.writeValueAsString(req);

		// when
		mockMvc
			.perform(post("/api/purchases")
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(content)
				.cookie(cookie)
			)
			//then
			.andExpect(status().isConflict());
	}

	@Test
	@DisplayName("구매 내역 일부 환불 > 티켓 환불 요청 > 성공")
	@Transactional
	void deletePurchaseTicketsSuccess() throws Exception {
		// given
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.SELLER);
		Member member = memberRepository.findAll().getFirst();
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);
		var ticketing = createTicketing(member, 0, 2);
		var purchaseTicketPair = createPurchase(member, ticketing, 2);
		var purchase = purchaseTicketPair.getFirst();
		var tickets = purchaseTicketPair.getSecond();

		List<UUID> ticketsToRefund = Collections.singletonList(tickets.getFirst().getId());
		DeletePurchaseTicketsRequestDto req = DeletePurchaseTicketsRequestDto.builder()
			.purchaseId(purchase.getId())
			.ticketIds(ticketsToRefund)
			.build();
		String content = objectMapper.writeValueAsString(req);

		// when
		mockMvc
			.perform(delete("/api/purchases/{purchaseId}/tickets", purchase.getId())
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(content)
				.cookie(cookie)
			)
			//then
			.andExpect(status().isOk());
		Assertions.assertThat(ticketRepository.findAllByPurchase(purchase).size()).isEqualTo(1);
	}

	@Test
	@DisplayName("구매 내역 전체 환불 > 티켓 환불 요청 > 성공")
	@Transactional
	void deletePurchaseAllTicketsSuccess() throws Exception {
		// given
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.SELLER);
		Member member = memberRepository.findAll().getFirst();
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);
		var ticketing = createTicketing(member, 0, 2);
		var purchaseTicketPair = createPurchase(member, ticketing, 2);
		var purchase = purchaseTicketPair.getFirst();
		var tickets = purchaseTicketPair.getSecond();

		List<UUID> ticketsToRefund = tickets.stream().map(Ticket::getId).toList();
		DeletePurchaseTicketsRequestDto req = DeletePurchaseTicketsRequestDto.builder()
			.purchaseId(purchase.getId())
			.ticketIds(ticketsToRefund)
			.build();
		String content = objectMapper.writeValueAsString(req);

		// when
		mockMvc
			.perform(delete("/api/purchases/{purchaseId}/tickets", purchase.getId())
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(content)
				.cookie(cookie)
			)
			//then
			.andExpect(status().isOk());
		Assertions.assertThat(purchaseRepository.findById(purchase.getId())).isEmpty();
		Assertions.assertThat(ticketRepository.findAllByPurchase(purchase).size()).isEqualTo(0);
	}

	@Test
	@DisplayName("구매 내역이 존재하지 않음 > 티켓 환불 요청 > 실패")
	@Transactional
	void deletePurchaseTicketsFailPurchaseNotFound() throws Exception {
		// given
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.SELLER);
		Member member = memberRepository.findAll().getFirst();
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);
		var notExistedPurchaseId = UUID.randomUUID();

		List<UUID> ticketsToRefund = Collections.singletonList(UUID.randomUUID());
		DeletePurchaseTicketsRequestDto req = DeletePurchaseTicketsRequestDto.builder()
			.purchaseId(notExistedPurchaseId)
			.ticketIds(ticketsToRefund)
			.build();
		String content = objectMapper.writeValueAsString(req);

		// when
		mockMvc
			.perform(delete("/api/purchases/{purchaseId}/tickets", notExistedPurchaseId)
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(content)
				.cookie(cookie)
			)
			//then
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("빈 구매 내역 > 티켓 환불 요청 > 실패")
	@Transactional
	void deletePurchaseTicketsFailEmptyPurchase() throws Exception {
		// given
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.SELLER);
		Member member = memberRepository.findAll().getFirst();
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);
		var ticketing = createTicketing(member, 0, 5);
		var purchaseTicketPair = createPurchase(member, ticketing, 0);
		var purchase = purchaseTicketPair.getFirst();

		List<UUID> ticketsToRefund = Collections.singletonList(UUID.randomUUID());
		DeletePurchaseTicketsRequestDto req = DeletePurchaseTicketsRequestDto.builder()
			.purchaseId(purchase.getId())
			.ticketIds(ticketsToRefund)
			.build();
		String content = objectMapper.writeValueAsString(req);

		// when
		mockMvc
			.perform(delete("/api/purchases/{purchaseId}/tickets", purchase.getId())
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(content)
				.cookie(cookie)
			)
			//then
			.andExpect(status().isConflict());
	}

	@Test
	@DisplayName("구매 내역 소유자가 아님 > 티켓 환불 요청 > 실패")
	@Transactional
	void deletePurchaseTicketsFailNotPurchaseOwner() throws Exception {
		// given
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.SELLER);
		Member member = testHelper.createMember("otherUser@example.com");
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);
		var ticketing = createTicketing(member, 0, 2);
		var purchaseTicketPair = createPurchase(member, ticketing, 2);
		var purchase = purchaseTicketPair.getFirst();
		var tickets = purchaseTicketPair.getSecond();

		List<UUID> ticketsToRefund = Collections.singletonList(tickets.getFirst().getId());
		DeletePurchaseTicketsRequestDto req = DeletePurchaseTicketsRequestDto.builder()
			.purchaseId(purchase.getId())
			.ticketIds(ticketsToRefund)
			.build();
		String content = objectMapper.writeValueAsString(req);

		// when
		mockMvc
			.perform(delete("/api/purchases/{purchaseId}/tickets", purchase.getId())
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(content)
				.cookie(cookie)
			)
			//then
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("티켓팅 판매 기간이 아님 > 티켓 환불 요청 > 실패")
	@Transactional
	void deletePurchaseTicketsFailNotTicketingSalePeriod() throws Exception {
		// given
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.SELLER);
		Member member = memberRepository.findAll().getFirst();
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);
		var ticketing = createTicketing(member, 1, 2);
		var purchaseTicketPair = createPurchase(member, ticketing, 2);
		var purchase = purchaseTicketPair.getFirst();
		var tickets = purchaseTicketPair.getSecond();

		List<UUID> ticketsToRefund = Collections.singletonList(tickets.getFirst().getId());
		DeletePurchaseTicketsRequestDto req = DeletePurchaseTicketsRequestDto.builder()
			.purchaseId(purchase.getId())
			.ticketIds(ticketsToRefund)
			.build();
		String content = objectMapper.writeValueAsString(req);

		// when
		mockMvc
			.perform(delete("/api/purchases/{purchaseId}/tickets", purchase.getId())
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(content)
				.cookie(cookie)
			)
			//then
			.andExpect(status().isBadRequest());
	}

	private Ticketing createTicketing(Member member, int saleStartAfterYears, int stock) {
		var now = LocalDateTime.now();
		var eventTime = now.plusYears(saleStartAfterYears + 2);
		var saleStart = now.plusYears(saleStartAfterYears);
		var saleEnd = now.plusYears(saleStartAfterYears + 1);
		var ticketing = ticketingRepository.save(Ticketing.builder()
			.price(1000)
			.title("test")
			.member(member)
			.description("")
			.location("Seoul")
			.eventTime(eventTime)
			.saleStart(saleStart)
			.saleEnd(saleEnd)
			.category("concert")
			.runningMinutes(300).build());
		ticketRepository.saveAll(Arrays.stream(new int[stock])
			.mapToObj(i -> Ticket.builder().ticketing(ticketing).build())
			.toList());
		return ticketing;
	}

	private Pair<Purchase, List<Ticket>> createPurchase(Member member, Ticketing ticketing, int count) {
		var purchase = this.purchaseRepository.save(Purchase.builder().member(member).build());

		if (count > 0) {
			var tickets = updateTicketPurchase(purchase, ticketing, count);
			return Pair.of(purchase, tickets);
		}
		return Pair.of(purchase, Collections.emptyList());
	}

	private List<Ticket> updateTicketPurchase(Purchase purchase, Ticketing ticketing, int count) {
		var tickets = this.ticketRepository.findByTicketingIdAndPurchaseIsNullOrderById(ticketing.getId(),
			Limit.of(count));
		if (tickets.size() < count) {
			throw new NotEnoughTicketException();
		}
		tickets.forEach(ticket -> {
			ticket.setPurchase(purchase);
			this.ticketRepository.save(ticket);
		});
		return tickets;
	}
}
