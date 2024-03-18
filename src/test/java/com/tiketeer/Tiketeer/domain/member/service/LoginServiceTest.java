package com.tiketeer.Tiketeer.domain.member.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tiketeer.Tiketeer.auth.jwt.JwtPayload;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.GenerateCookieCommand;
import com.tiketeer.Tiketeer.domain.role.Role;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

import jakarta.servlet.http.Cookie;

@Import({TestHelper.class})
@SpringBootTest
@AutoConfigureMockMvc
class LoginServiceTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private MemberService memberService;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private LoginService loginService;
	@Autowired
	private JwtService jwtService;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("dd")
	void loginSuccess() {

		//given
		Role role = roleRepository.findByName(RoleEnum.BUYER).orElseThrow();

		Member member = Member.builder()
			.email("user@example.com")
			.password(passwordEncoder.encode("password"))
			.point(0)
			.enabled(true)
			.role(role)
			.build();

		memberRepository.save(member);

		GenerateCookieCommand command = GenerateCookieCommand.builder()
			.email("user@example.com")
			.password("password")
			.build();

		//when
		Cookie cookie = loginService.generateCookie(command);

		//then
		String token = cookie.getValue();
		JwtPayload jwtPayload = jwtService.verifyToken(token);
		assertThat(jwtPayload.email()).isEqualTo("user@example.com");
		assertThat(jwtPayload.roleEnum()).isEqualTo(RoleEnum.BUYER);

	}
}