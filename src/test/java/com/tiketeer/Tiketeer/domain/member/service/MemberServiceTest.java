package com.tiketeer.Tiketeer.domain.member.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.auth.jwt.JwtPayload;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.Otp;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidOtpException;
import com.tiketeer.Tiketeer.domain.member.exception.InvalidTokenException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.member.service.dto.GetMemberPurchasesCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.InitMemberPasswordWithOtpCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.RefreshAccessTokenCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.RefreshAccessTokenResultDto;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
public class MemberServiceTest {
	@Autowired
	private TestHelper testHelper;

	@Autowired
	private MemberService memberService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private OtpRepository otpRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private TicketRepository ticketRepository;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("유효하지 않은 OTP > MemberService.initPasswordWithOtp 호출 > 실패")
	@Transactional
	void initPasswordWithOtpFailBecauseInvalidOtp() {
		// given
		var mockEmail = "test@test.com";
		var member = createMember(mockEmail);

		otpRepository.save(
			Otp.builder()
				.member(member)
				.expiredAt(LocalDateTime.of(9999, 12, 31, 0, 0))
				.build());

		var invalidOtp = UUID.randomUUID();

		Assertions.assertThatThrownBy(() -> {
			// when
			memberService.initPasswordWithOtp(InitMemberPasswordWithOtpCommandDto.builder().otp(invalidOtp).build());
			// then
		}).isInstanceOf(InvalidOtpException.class);
	}

	@Test
	@DisplayName("유효한 OTP > MemberService.initPasswordWithOtp 호출 > 성공")
	@Transactional
	void initPasswordWithOtpSuccess() {
		// given
		var mockEmail = "test@test.com";
		var member = createMember(mockEmail);

		var otp = otpRepository.save(
			Otp.builder()
				.member(member)
				.expiredAt(LocalDateTime.of(9999, 12, 31, 0, 0))
				.build());

		var mockPwd = "1qwertasdcxz";

		// when
		memberService.initPasswordWithOtp(
			InitMemberPasswordWithOtpCommandDto.builder().otp(otp.getPassword()).password(mockPwd).build());

		// then
		var memberAfterEmailAuthOpt = memberRepository.findByEmail(mockEmail);
		Assertions.assertThat(memberAfterEmailAuthOpt.isPresent()).isTrue();

		var memberAfterEmailAuth = memberAfterEmailAuthOpt.get();
		Assertions.assertThat(memberAfterEmailAuth.isEnabled()).isTrue();
		Assertions.assertThat(passwordEncoder.matches(mockPwd, memberAfterEmailAuth.getPassword())).isTrue();

		Assertions.assertThat(otpRepository.findById(otp.getPassword()).isPresent()).isFalse();
	}

	private Member createMember(String email) {
		var role = roleRepository.findByName(RoleEnum.BUYER).orElseThrow();
		var memberForSave = Member.builder()
			.email(email)
			.password("1234456eqeqw").role(role).build();
		return memberRepository.save(memberForSave);
	}

	@Test
	@DisplayName("정상 토큰 > 재발급 > 재발급 확인")
	void refreshAccessToken() {
		// given
		String refreshToken = jwtService.createToken(
			new JwtPayload("test@gmail.com", RoleEnum.BUYER, new Date(System.currentTimeMillis())));

		// when
		RefreshAccessTokenResultDto refreshAccessTokenResultDto = memberService.refreshAccessToken(
			RefreshAccessTokenCommandDto.builder().refreshToken(refreshToken).build());

		// then
		String accessToken1 = refreshAccessTokenResultDto.getAccessToken();
		JwtPayload jwtPayload = jwtService.verifyToken(accessToken1);

		Assertions.assertThat(accessToken1).isNotNull();
		Assertions.assertThat(jwtPayload.email()).isEqualTo("test@gmail.com");
	}

	@Test
	@DisplayName("refreshToken 만료 > 재발급 > 예외발생 확인")
	void refreshAccessTokenFailByExpiredRefreshToken() {
		// given
		String refreshToken = jwtService.createToken(
			new JwtPayload("test@gmail.com", RoleEnum.BUYER,
				new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)));

		// when
		// then
		assertThrows(InvalidTokenException.class, () -> {
			memberService.refreshAccessToken(
				RefreshAccessTokenCommandDto.builder().refreshToken(refreshToken).build());
		});
	}

	@Test
	@DisplayName("정상 조건 > 멤버 구매 내역 조회 요청 > 성공")
	@Transactional
	void getMemberPurchases() {
		// given
		var mockEmail = "test@test.com";
		var member = createMember(mockEmail);
		var now = LocalDateTime.now();
		var ticketing1 = ticketingRepository.save(
			new Ticketing(1000, member, "", "test1", "Seoul", now, "", 600, now, now));
		var ticketing2 = ticketingRepository.save(
			new Ticketing(1000, member, "", "test2", "Seoul", now, "", 600, now, now));
		var purchase1 = purchaseRepository.save(new Purchase(member));
		var purchase2 = purchaseRepository.save(new Purchase(member));
		ticketRepository.save(new Ticket(null, ticketing1));
		ticketRepository.save(new Ticket(purchase1, ticketing1));
		ticketRepository.save(new Ticket(purchase1, ticketing1));
		ticketRepository.save(new Ticket(purchase2, ticketing2));

		// when
		var results = memberService.getMemberPurchases(
			GetMemberPurchasesCommandDto.builder().memberEmail(mockEmail).build());

		// then
		Assertions.assertThat(results.size()).isEqualTo(2);
		Assertions.assertThat(results.get(0).getCount()).isEqualTo(2);
		Assertions.assertThat(results.get(0).getTicketingId()).isEqualTo(ticketing1.getId());
		Assertions.assertThat(results.get(1).getCount()).isEqualTo(1);
		Assertions.assertThat(results.get(1).getTicketingId()).isEqualTo(ticketing2.getId());
	}
}
