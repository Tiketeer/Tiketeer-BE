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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiketeer.Tiketeer.auth.constant.JwtMetadata;
import com.tiketeer.Tiketeer.domain.member.controller.dto.LoginRequestDto;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	void initDB() {
		testHelper.initDB();
		testHelper.createMember("user@example.com", "password");
	}

	@AfterEach
	void cleanDB() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("DB 내 계정 존재 > 로그인 요청 > 성공")
	void loginSuccess() throws Exception {

		LoginRequestDto loginRequestDto = LoginRequestDto.builder()
			.email("user@example.com")
			.password("password")
			.build();

		mockMvc
			.perform(post("/api/auth/login")
				.contextPath("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(objectMapper.writeValueAsString(loginRequestDto)))

			.andExpect(status().isOk())
			.andExpect(cookie().exists(JwtMetadata.ACCESS_TOKEN));
	}

}