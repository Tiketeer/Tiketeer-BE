package com.tiketeer.Tiketeer.domain.member.usecase;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.tiketeer.Tiketeer.domain.member.exception.MemberNotFoundException;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.GetMemberCommandDto;
import com.tiketeer.Tiketeer.testhelper.TestHelper;

@Import({TestHelper.class})
@SpringBootTest
class GetMemberUseCaseTest {
	@Autowired
	private TestHelper testHelper;

	@Autowired
	private GetMemberUseCase getMemberUseCase;

	@BeforeEach
	void initTable() {
		testHelper.initDB();
	}

	@AfterEach
	void cleanTable() {
		testHelper.cleanDB();
	}

	@Test
	@DisplayName("정상 조건 > 멤버 조회 > 성공")
	@Transactional
	void getMemberSuccess() {
		// given
		var mockEmail = "test@test.com";
		var memberInDb = testHelper.createMember(mockEmail);

		var command = GetMemberCommandDto.builder().memberEmail(mockEmail).build();

		// when
		var member = getMemberUseCase.get(command);

		// then
		Assertions.assertThat(member.getEmail()).isEqualTo(memberInDb.getEmail());
		Assertions.assertThat(member.getPoint()).isEqualTo(memberInDb.getPoint());
		Assertions.assertThat(member.getProfileUrl()).isEqualTo(memberInDb.getProfileUrl());
		Assertions.assertThat(member.getCreatedAt()).isEqualTo(memberInDb.getCreatedAt());
	}

	@Test
	@DisplayName("멤버가 존재하지 않음 > 멤버 조회 > 실패")
	@Transactional(readOnly = true)
	void getMemberFailNotFoundMember() {
		// given
		var mockEmail = "test@test.com";

		var command = GetMemberCommandDto.builder().memberEmail(mockEmail).build();

		Assertions.assertThatThrownBy(() -> {
			// when
			getMemberUseCase.get(command);
			// then
		}).isInstanceOf(MemberNotFoundException.class);
	}
}