package com.tiketeer.Tiketeer.domain.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.controller.dto.ChargePointRequestDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.MemberRegisterRequestDto;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private RoleRepository roleRepository;

	@BeforeEach
	void initDB() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanDB() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("회원정보 전달 > 회원가입 컨트롤러에 요청 > DB 회원 정보와 비교")
	void registerMember() throws Exception {
		MemberRegisterRequestDto dto = MemberRegisterRequestDto.builder()
			.email("test@mail.com")
			.isSeller(false)
			.build();

		mockMvc.perform(
				post("/api/members/register")
					.contextPath("/api").contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("utf-8")
					.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isOk())
			.andExpect(
				jsonPath("$.data.memberId").value(
					memberRepository.findByEmail("test@mail.com").orElseThrow().getId().toString()));
	}

	@Test
	@DisplayName("포인트 충전량 정보 > 포인트 충전 컨트롤러에 요청 > point 검증")
	void chargePoint() throws Exception {
		ChargePointRequestDto dto = ChargePointRequestDto.builder().pointForCharge(1000L).build();
		Role role = roleRepository.findByName(RoleEnum.BUYER).orElseThrow();
		Member saved = memberRepository.save(new Member("mock@mock.com", "asdf1234", 0L, true, null, role));

		mockMvc.perform(post("/api/members/" + saved.getId() + "/points").contextPath("/api")
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