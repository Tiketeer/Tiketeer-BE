package com.tiketeer.Tiketeer.domain.member.controller;

import static java.time.LocalDateTime.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.temporal.ChronoUnit;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.auth.constant.JwtMetadata;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
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
class MemberControllerTest {

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
	@DisplayName("멤버/티케팅/티켓 생성 + 구매 > 멤버 판매목록 조회 > 성공적으로 반환")
	void getMemberTicketingSales() throws Exception {
		//given
		var now = now().truncatedTo(ChronoUnit.SECONDS);
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.BUYER);
		Member member = memberRepository.findAll().getFirst();
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);
		var ticketing = ticketingRepository.save(
			new Ticketing(1000, member, "", "test", "Seoul", now, "", 600, now, now));
		var purchase = purchaseRepository.save(new Purchase(member));
		ticketRepository.save(new Ticket(null, ticketing));
		ticketRepository.save(new Ticket(purchase, ticketing));
		ticketRepository.save(new Ticket(purchase, ticketing));

		//when - then

		mockMvc.perform(MockMvcRequestBuilders.get("/api/members/" + member.getId() + "/sale")
				.contextPath("/api")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.cookie(cookie)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].price").value(1000))
			.andExpect(jsonPath("$.data[0].description").value(""))
			.andExpect(jsonPath("$.data[0].title").value("test"))
			.andExpect(jsonPath("$.data[0].location").value("Seoul"))
			.andExpect(jsonPath("$.data[0].eventTime").value(now.toString()))
			.andExpect(jsonPath("$.data[0].saleStart").value(now.toString()))
			.andExpect(jsonPath("$.data[0].saleEnd").value(now.toString()))
			.andExpect(jsonPath("$.data[0].stock").value(3))
			.andExpect(jsonPath("$.data[0].remainStock").value(1))
			.andExpect(jsonPath("$.data[0].category").value(""))
			.andExpect(jsonPath("$.data[0].runningMinutes").value(600));
	}
}