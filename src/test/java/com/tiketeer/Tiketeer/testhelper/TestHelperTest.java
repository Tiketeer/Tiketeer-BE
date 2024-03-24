package com.tiketeer.Tiketeer.testhelper;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tiketeer.Tiketeer.auth.jwt.JwtService;
import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.Otp;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.repository.OtpRepository;
import com.tiketeer.Tiketeer.domain.purchase.Purchase;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.role.constant.PermissionEnum;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.PermissionRepository;
import com.tiketeer.Tiketeer.domain.role.repository.RolePermissionRepository;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.Ticket;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;
import com.tiketeer.Tiketeer.response.ApiResponse;

@Import({TestHelper.class})
@SpringBootTest
public class TestHelperTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private RolePermissionRepository rolePermissionRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private OtpRepository otpRepository;
	@Autowired
	private PurchaseRepository purchaseRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private TicketingRepository ticketingRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtService jwtService;

	@AfterEach
	void clearTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("빈 DB > TestHelper.initDB 호출 > DB 내 Role, Permission 생성")
	void initDB() {
		// given
		// when
		testHelper.initDB();

		// then
		var permitList = permissionRepository.findAll();
		assertThat(permitList.size()).isEqualTo(PermissionEnum.values().length);

		var permissionNameList = Arrays.stream(PermissionEnum.values()).map(PermissionEnum::name).toList();
		permitList.forEach(permit -> isInTest(permissionNameList, permit.getName().name()));

		var roleList = roleRepository.findAll();
		assertThat(roleList.size()).isEqualTo(RoleEnum.values().length);

		var roleNameList = Arrays.stream(RoleEnum.values()).map(RoleEnum::name).toList();
		roleList.forEach(role -> isInTest(roleNameList, role.getName().name()));
	}

	@Test
	@DisplayName("DB 내 데이터 존재 > TestHelper.cleanDB 호출 > DB 내 모든 테이블이 빔")
	@Transactional
	void cleanDB() {
		// given
		testHelper.initDB();

		var role = roleRepository.findAll().getFirst();

		var mockEmail = "test@test.com";
		var mockPwd = "1234sdasdf";
		var mockMember = memberRepository.save(Member.builder()
			.email(mockEmail)
			.password(mockPwd)
			.point(0L)
			.enabled(false)
			.role(role)
			.build());

		otpRepository.save(Otp.builder().member(mockMember).expiredAt(LocalDateTime.of(9999, 12, 31, 0, 0)).build());

		var mockTicketing = ticketingRepository.save(Ticketing.builder()
			.price(10000)
			.member(mockMember)
			.title("Mock Ticketing")
			.location("서울 어딘가")
			.category("몰라")
			.eventTime(LocalDateTime.of(9999, 12, 31, 0, 0))
			.runningMinutes(999)
			.saleStart(LocalDateTime.of(9999, 11, 1, 0, 0))
			.saleEnd(LocalDateTime.of(9999, 11, 30, 0, 0))
			.build());

		var mockPurchase = purchaseRepository.save(Purchase.builder().member(mockMember).build());

		ticketRepository.save(Ticket.builder().ticketing(mockTicketing).purchase(mockPurchase).build());

		var repoForTestList = List.of(
			ticketingRepository,
			purchaseRepository,
			ticketRepository,
			memberRepository,
			otpRepository,
			rolePermissionRepository,
			roleRepository,
			permissionRepository
		);

		repoForTestList.forEach(repo -> {
			assertThat(repo.findAll()).isNotEmpty();
		});

		// when
		testHelper.cleanDB();

		// then
		repoForTestList.forEach(repo -> {
			assertThat(repo.findAll()).isEmpty();
		});
	}

	@Test
	@DisplayName("이메일만 지정 > 멤버 생성 요청 > 이메일만 지정된 멤버 생성 (나머지는 메서드 내 기본 값)")
	@Transactional
	void createMemberEmailParamSuccess() {
		// given
		testHelper.initDB();

		var email = "test@test.com";

		// when
		var memberId = testHelper.createMember(email).getId();

		// then
		var memberOpt = memberRepository.findById(memberId);
		assertThat(memberOpt.isPresent()).isTrue();

		var member = memberOpt.get();
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(passwordEncoder.matches("1q2w3e4r!!", member.getPassword()));
		assertThat(member.getRole().getName()).isEqualTo(RoleEnum.BUYER);
		defaultMemberPropertiesTest(member);
	}

	@Test
	@DisplayName("이메일, 패스워드 지정 > 멤버 생성 요청 > 이메일, 패스워드가 지정된 멤버 생성 (나머지는 메서드 내 기본 값)")
	@Transactional
	void createMemberEmailAndPasswordParamSuccess() {
		// given
		testHelper.initDB();

		var email = "test@test.com";
		var password = "qwerty12345!@#$";

		// when
		var memberId = testHelper.createMember(email, password).getId();

		// then
		var memberOpt = memberRepository.findById(memberId);
		assertThat(memberOpt.isPresent()).isTrue();

		var member = memberOpt.get();
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(passwordEncoder.matches(password, member.getPassword()));
		assertThat(member.getRole().getName()).isEqualTo(RoleEnum.BUYER);
		defaultMemberPropertiesTest(member);
	}

	@Test
	@DisplayName("이메일, 패스워드, 역할 지정 > 멤버 생성 요청 > 이메일, 패스워드, 역할이 지정된 멤버 생성 (나머지는 메서드 내 기본 값)")
	@Transactional
	void createMemberEmailAndPasswordAndRoleParamSuccess() {
		// given
		testHelper.initDB();

		var email = "test@test.com";
		var password = "qwerty12345!@#$";
		var roleEnum = RoleEnum.SELLER;

		// when
		var memberId = testHelper.createMember(email, password, roleEnum).getId();

		// then
		var memberOpt = memberRepository.findById(memberId);
		assertThat(memberOpt.isPresent()).isTrue();

		var member = memberOpt.get();
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(passwordEncoder.matches(password, member.getPassword()));
		assertThat(member.getRole().getName()).isEqualTo(roleEnum);
		defaultMemberPropertiesTest(member);
	}

	@Test
	@DisplayName("이메일, 역할 지정 > 멤버 생성, 로그인, 액세스 토큰 반환 요청 > 성공")
	@Transactional
	void registerAndLoginReturnAccessTokenSuccess() {
		// given
		testHelper.initDB();

		var email = "test@test.com";
		var roleEnum = RoleEnum.SELLER;

		// when
		var accessToken = testHelper.registerAndLoginAndReturnAccessToken(email, roleEnum);

		// then
		var memberOpt = memberRepository.findByEmail(email);
		assertThat(memberOpt.isPresent()).isTrue();

		var member = memberOpt.get();
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(passwordEncoder.matches("1q2w3e4r!!", member.getPassword()));
		assertThat(member.getRole().getName()).isEqualTo(roleEnum);
		defaultMemberPropertiesTest(member);

		var payload = jwtService.verifyToken(accessToken);
		assertThat(payload.email()).isEqualTo(email);
		assertThat(payload.roleEnum()).isEqualTo(roleEnum);
	}

	private <T> void isInTest(Iterable<T> iterable, T target) {
		assertThat(target).isIn(iterable);
	}

	private void defaultMemberPropertiesTest(Member member) {
		assertThat(member.getPoint()).isEqualTo(0);
		assertThat(member.isEnabled()).isTrue();
		assertThat(member.getProfileUrl()).isNull();
	}

	private record DeserializeTestClass(String name) {
	}

	@Test
	void getDeserializedListApiResponseSuccess() throws JsonProcessingException {
		String json = "{\"data\":[{\"name\":\"test1\"},{\"name\":\"test2\"}]}";

		ApiResponse<List<DeserializeTestClass>> deserializedListApiResponse = testHelper.getDeserializedListApiResponse(
			json, DeserializeTestClass.class);

		DeserializeTestClass test1 = deserializedListApiResponse.getData().get(0);
		DeserializeTestClass test2 = deserializedListApiResponse.getData().get(1);

		assertThat(test1.name()).isEqualTo("test1");
		assertThat(test2.name()).isEqualTo("test2");
	}

	@Test
	void getDeserializedApiResponseSuccess() throws JsonProcessingException {
		String json = "{\"data\":{\"name\":\"test1\"}}";

		ApiResponse<DeserializeTestClass> deserializedApiResponse = testHelper.getDeserializedApiResponse(
			json, DeserializeTestClass.class);

		DeserializeTestClass test1 = deserializedApiResponse.getData();

		assertThat(test1.name()).isEqualTo("test1");
	}
}
