package com.tiketeer.Tiketeer.auth;

import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;

public class RequestMatcherHolderTest {
	private RequestMatcherHolder requestMatcherHolder;

	@BeforeEach
	void injectRequestMatcherHolder() {
		requestMatcherHolder = new RequestMatcherHolder();
	}

	@Test
	@DisplayName("퍼블릭 EP > 최소 권한이 null 인 RequestMatcher 요청 > matches = true")
	void getRequestMatchersByMinRoleSuccessNullCase() {
		// given
		List<String> publicEPList = List.of("/ticketings", "/ticketings/" + UUID.randomUUID());

		// when
		var requestMatcher = requestMatcherHolder.getRequestMatchersByMinRole(null);

		// then
		publicEPList.forEach(path -> {
			var request = new MockHttpServletRequest("GET", path);
			request.setPathInfo(path);
			Assertions.assertThat(requestMatcher.matches(request)).isTrue();
		});
	}

	@Test
	@DisplayName("퍼블릭 EP > 최소 권한이 Buyer 인 RequestMatcher 요청 > matches = true")
	void getRequestMatchersByMinRoleSuccessBuyerCase() {
		// given
		List<String> publicEPList = List.of("/members/" + UUID.randomUUID(),
			"/members/" + UUID.randomUUID() + "/purchases");

		// when
		var requestMatcher = requestMatcherHolder.getRequestMatchersByMinRole(RoleEnum.BUYER);

		// then
		publicEPList.forEach(path -> {
			var request = new MockHttpServletRequest("GET", path);
			request.setPathInfo(path);
			Assertions.assertThat(requestMatcher.matches(request)).isTrue();
		});
	}

	@Test
	@DisplayName("퍼블릭 EP > 최소 권한이 Seller 인 RequestMatcher 요청 > matches = true")
	void getRequestMatchersByMinRoleSuccessSellerCase() {
		// given
		List<String> publicEPList = List.of("/members/" + UUID.randomUUID() + "/sale");

		// when
		var requestMatcher = requestMatcherHolder.getRequestMatchersByMinRole(RoleEnum.SELLER);

		// then
		publicEPList.forEach(path -> {
			var request = new MockHttpServletRequest("GET", path);
			request.setPathInfo(path);
			Assertions.assertThat(requestMatcher.matches(request)).isTrue();
		});
	}

	@Test
	@DisplayName("이미 null을 인자로 RequestMatcher 생성 > null을 인자로 get 재호출 > 두 RequestMatcher의 동등 판정")
	void getRequestMatcherByMinRoleSuccessForCache() {
		// given
		var requestMatcher1 = requestMatcherHolder.getRequestMatchersByMinRole(null);

		// when
		var requestMatcher2 = requestMatcherHolder.getRequestMatchersByMinRole(null);

		// then
		Assertions.assertThat(requestMatcher1).isEqualTo(requestMatcher2);
	}
}
