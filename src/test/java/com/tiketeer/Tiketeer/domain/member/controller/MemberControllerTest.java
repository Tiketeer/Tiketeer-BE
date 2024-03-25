package com.tiketeer.Tiketeer.domain.member.controller;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiketeer.Tiketeer.auth.constant.JwtMetadata;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.Otp;
import com.tiketeer.Tiketeer.domain.member.controller.dto.ChargePointRequestDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.GetMemberTicketingSalesResponseDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.ResetPasswordRequestDto;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.response.ApiResponse;
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
	@Autowired
	private ObjectMapper objectMapper;

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
	void getMemberTicketingSalesSuccess() throws Exception {
		//given
		var now = LocalDateTime.of(2024, 1, 1, 1, 1, 1);
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.SELLER);
		Member member = memberRepository.findAll().getFirst();
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);
		var ticketing = ticketingRepository.save(
			new Ticketing(1000, member, "", "test", "Seoul", now, "", 600, now, now));
		var purchase = purchaseRepository.save(new Purchase(member));
		ticketRepository.save(new Ticket(null, ticketing));
		ticketRepository.save(new Ticket(purchase, ticketing));
		ticketRepository.save(new Ticket(purchase, ticketing));

		//when - then
		MvcResult result = mockMvc.perform(get("/api/members/" + member.getId() + "/sale")
				.contextPath("/api")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.cookie(cookie)
			)
			.andExpect(status().isOk())
			.andReturn();

		String jsonResult = result.getResponse().getContentAsString();

		ApiResponse<List<GetMemberTicketingSalesResponseDto>> apiResponse = testHelper.getDeserializedListApiResponse(
			jsonResult, GetMemberTicketingSalesResponseDto.class);

		var dto = apiResponse.getData().getFirst();

		assertThat(dto.getPrice()).isEqualTo(1000);
		assertThat(dto.getDescription()).isEqualTo("");
		assertThat(dto.getTitle()).isEqualTo("test");
		assertThat(dto.getLocation()).isEqualTo("Seoul");
		assertThat(dto.getEventTime()).isEqualToIgnoringNanos(now);
		assertThat(dto.getSaleStart()).isEqualToIgnoringNanos(now);
		assertThat(dto.getSaleEnd()).isEqualToIgnoringNanos(now);
		assertThat(dto.getStock()).isEqualTo(3);
		assertThat(dto.getRemainStock()).isEqualTo(1);
		assertThat(dto.getCategory()).isEqualTo("");
		assertThat(dto.getRunningMinutes()).isEqualTo(600);

	}

	@Test
	@Transactional
	@DisplayName("유저 회원가입 및 로그인 > 비밀번호 변경 > 변경 확인")
	void resetPasswordSuccess() throws Exception {

		//given
		var now = now().truncatedTo(ChronoUnit.SECONDS);
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.SELLER);
		Member member = memberRepository.findAll().getFirst();
		Otp otp = testHelper.createOtp(member, now().plusDays(1));
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);

		//when - then
		ResetPasswordRequestDto req = new ResetPasswordRequestDto(otp.getPassword(), "newpassword");
		String content = objectMapper.writeValueAsString(req);

		mockMvc.perform(put("/api/members/password")
			.contextPath("/api")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.characterEncoding("utf-8")
			.cookie(cookie)
			.content(content)
		).andExpect(status().isOk());
	}

	@Test
	@Transactional
	@DisplayName("유저 회원가입 및 로그인 > 동일한 비밀번호로 변경 > 변경 실패")
	void resetPasswordFailSamePassword() throws Exception {

		//given
		var now = now().truncatedTo(ChronoUnit.SECONDS);
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.SELLER);
		Member member = memberRepository.findAll().getFirst();
		Otp otp = testHelper.createOtp(member, now().plusDays(1));
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);

		//when - then
		ResetPasswordRequestDto req = new ResetPasswordRequestDto(otp.getPassword(), "1q2w3e4r!!");
		String content = objectMapper.writeValueAsString(req);

		mockMvc.perform(put("/api/members/password")
			.contextPath("/api")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.characterEncoding("utf-8")
			.cookie(cookie)
			.content(content)
		).andExpect(status().isConflict());
	}

	@Test
	@Transactional
	@DisplayName("유저 회원가입 및 로그인 > 만료된 OTP > 변경 실패")
	void resetPasswordFailExpiredOtp() throws Exception {

		//given
		var now = now().truncatedTo(ChronoUnit.SECONDS);
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.SELLER);
		Member member = memberRepository.findAll().getFirst();
		Otp otp = testHelper.createOtp(member, now().minusDays(1));
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);

		//when - then
		ResetPasswordRequestDto req = new ResetPasswordRequestDto(otp.getPassword(), "1q2w3e4r!!");
		String content = objectMapper.writeValueAsString(req);

		mockMvc.perform(put("/api/members/password")
			.contextPath("/api")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.characterEncoding("utf-8")
			.cookie(cookie)
			.content(content)
		).andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("포인트 충전량 정보 > 포인트 충전 컨트롤러에 요청 > point 검증")
	void chargePoint() throws Exception {
		String token = testHelper.registerAndLoginAndReturnAccessToken("mock@mock.com", RoleEnum.SELLER);
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);

		ChargePointRequestDto dto = ChargePointRequestDto.builder().pointForCharge(1000L).build();
		Member member = memberRepository.findByEmail("mock@mock.com").orElseThrow();

		mockMvc.perform(post("/api/members/" + member.getId() + "/points")
				.cookie(cookie)
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(dto))
			)
			.andExpect(status().isOk())
			.andExpect(
				jsonPath("$.data.totalPoint").value(1000L)
			);
	}
}