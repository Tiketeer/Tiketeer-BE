package com.tiketeer.Tiketeer.domain.member.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiketeer.Tiketeer.auth.constant.JwtMetadata;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

import jakarta.servlet.http.Cookie;

@Import({TestHelper.class})
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TestHelper testHelper;
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
	void getMemberTicketingSales() throws Exception {
		String token = testHelper.registerAndLoginAndReturnAccessToken("user@example.com", RoleEnum.BUYER);
		Member member = memberRepository.findAll().getFirst();
		Cookie cookie = new Cookie(JwtMetadata.ACCESS_TOKEN, token);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/members/" + member.getId() + "/sale")
			.contextPath("/api")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.characterEncoding("utf-8")
			.cookie(cookie)
		).andExpect(status().isOk());

	}
}