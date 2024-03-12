package com.tiketeer.Tiketeer.testhelper;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.Member;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.purchase.repository.PurchaseRepository;
import com.tiketeer.Tiketeer.domain.role.constant.PermissionEnum;
import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;
import com.tiketeer.Tiketeer.domain.role.repository.PermissionRepository;
import com.tiketeer.Tiketeer.domain.role.repository.RolePermissionRepository;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.domain.ticket.repository.TicketRepository;
import com.tiketeer.Tiketeer.domain.ticketing.Ticketing;
import com.tiketeer.Tiketeer.domain.ticketing.repository.TicketingRepository;

@SpringBootTest
public class TestHelperTest {
	private final TestHelper testHelper;
	private final PermissionRepository permissionRepository;
	private final RoleRepository roleRepository;
	private final RolePermissionRepository rolePermissionRepository;
	private final MemberRepository memberRepository;
	private final PurchaseRepository purchaseRepository;
	private final TicketRepository ticketRepository;
	private final TicketingRepository ticketingRepository;

	@Autowired
	public TestHelperTest(TestHelper testHelper, PermissionRepository permissionRepository,
		RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository,
		MemberRepository memberRepository, PurchaseRepository purchaseRepository, TicketRepository ticketRepository,
		TicketingRepository ticketingRepository) {
		this.testHelper = testHelper;
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository;
		this.rolePermissionRepository = rolePermissionRepository;
		this.memberRepository = memberRepository;
		this.purchaseRepository = purchaseRepository;
		this.ticketingRepository = ticketingRepository;
		this.ticketRepository = ticketRepository;
	}

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
		Assertions.assertThat(permitList.size()).isEqualTo(PermissionEnum.values().length);

		var permissionNameList = Arrays.stream(PermissionEnum.values()).map(PermissionEnum::name).toList();
		for (var permit : permitList) {
			isInTest(permissionNameList, permit.getName().name());
		}

		var roleList = roleRepository.findAll();
		Assertions.assertThat(roleList.size()).isEqualTo(RoleEnum.values().length);

		var roleNameList = Arrays.stream(RoleEnum.values()).map(RoleEnum::name).toList();
		for (var role : roleList) {
			isInTest(roleNameList, role.getName().name());
		}
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

		ticketingRepository.save(Ticketing.builder()
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

		// when
		testHelper.cleanDB();

		// then
		Assertions.assertThat(ticketingRepository.findAll()).isEmpty();
		Assertions.assertThat(memberRepository.findAll()).isEmpty();
		Assertions.assertThat(rolePermissionRepository.findAll()).isEmpty();
		Assertions.assertThat(roleRepository.findAll()).isEmpty();
		Assertions.assertThat(permissionRepository.findAll()).isEmpty();
	}

	private <T> void isInTest(Iterable<T> iterable, T target) {
		Assertions.assertThat(target).isIn(iterable);
	}
}
