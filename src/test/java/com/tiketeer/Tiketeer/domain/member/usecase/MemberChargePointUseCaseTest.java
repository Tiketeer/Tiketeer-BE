package com.tiketeer.Tiketeer.domain.member.usecase;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.InvalidPointChargeRequestException;
import com.tiketeer.Tiketeer.domain.member.exception.MemberIdAndAuthNotMatchedException;
import com.tiketeer.Tiketeer.domain.member.repository.MemberRepository;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.ChargePointCommandDto;
import com.tiketeer.Tiketeer.domain.role.repository.RoleRepository;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
class MemberChargePointUseCaseTest {
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private MemberChargePointUseCase memberChargePointUseCase;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private RoleRepository roleRepository;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("요청 충전 포인트가 음수 > 충전 요청 > 실패")
	@Transactional
	void chargePointFailBecauseMinusPointRequest() {
		// given
		var email = "test@test.com";
		var initPoint = 0L;
		var member = testHelper.createMember(email);
		member.setPoint(initPoint);

		var pointForCharge = -1000L;
		var command = ChargePointCommandDto.builder()
			.memberId(member.getId())
			.email(email)
			.pointForCharge(pointForCharge)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			memberChargePointUseCase.chargePoint(command);
			// then
		}).isInstanceOf(InvalidPointChargeRequestException.class);
	}

	@Test
	@DisplayName("요청 memberId와 토큰의 이메일이 매칭되지 않음 > 충전 요청 > 실패")
	@Transactional
	void chargePointFailBecauseMemberIdAndEmailNotMatched() {
		// given
		var email = "test@test.com";
		var initPoint = 0L;
		var member = testHelper.createMember(email);
		member.setPoint(initPoint);

		var anotherUuid = UUID.randomUUID();
		var pointForCharge = 1000L;
		var command = ChargePointCommandDto.builder()
			.memberId(anotherUuid)
			.email(email)
			.pointForCharge(pointForCharge)
			.build();

		Assertions.assertThatThrownBy(() -> {
			// when
			memberChargePointUseCase.chargePoint(command);
			// then
		}).isInstanceOf(MemberIdAndAuthNotMatchedException.class);
	}

	@Test
	@DisplayName("정상 컨디션(기존 포인트: 1000, 충전 요청: 3000) > 충전 요청 > 성공")
	@Transactional
	void chargePointSuccess() {
		// given
		var email = "test@test.com";
		var initPoint = 1000L;
		var member = testHelper.createMember(email);
		member.setPoint(initPoint);

		var pointForCharge = 3000L;
		var command = ChargePointCommandDto.builder()
			.memberId(member.getId())
			.email(email)
			.pointForCharge(pointForCharge)
			.build();

		// when
		var result = memberChargePointUseCase.chargePoint(command);

		// then
		Assertions.assertThat(result.getTotalPoint()).isEqualTo(initPoint + pointForCharge);

		var memberInDB = memberRepository.findByEmail(email).orElseThrow();
		Assertions.assertThat(memberInDB.getPoint()).isEqualTo(initPoint + pointForCharge);
	}

	// TODO: chargePoint에 대한 동시성 고려, 테스트 작성 필요
}