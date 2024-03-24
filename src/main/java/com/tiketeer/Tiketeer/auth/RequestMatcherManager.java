package com.tiketeer.Tiketeer.auth;

import static org.springframework.http.HttpMethod.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;

import jakarta.annotation.Nullable;

@Component
public class RequestMatcherManager {
	private final ConcurrentHashMap<String, RequestMatcher> reqMatcherCacheMap = new ConcurrentHashMap<>();

	/**
	 * if role == null, return permitAll Path
	 */
	public RequestMatcher getRequestMatchersByMinRole(@Nullable RoleEnum minRole) {
		var key = getKeyByRole(minRole);
		if (!reqMatcherCacheMap.containsKey(key)) {
			var requestMatcherByMinRole = new OrRequestMatcher(REQUEST_INFO_LIST.stream()
				.filter(reqInfo -> {
					if (reqInfo.minRole() == null) {
						return minRole == null;
					}
					return reqInfo.minRole().equals(minRole);
				})
				.map(reqInfo -> new AntPathRequestMatcher(reqInfo.pattern(), reqInfo.method().name()))
				.toArray(AntPathRequestMatcher[]::new));
			reqMatcherCacheMap.put(key, requestMatcherByMinRole);
		}
		return reqMatcherCacheMap.get(key);
	}

	private String getKeyByRole(@Nullable RoleEnum minRole) {
		if (minRole == null) {
			return "VISITOR";
		}
		return minRole.name();
	}

	private static final List<RequestInfo> REQUEST_INFO_LIST = List.of(
		// member
		new RequestInfo(POST, "/members/register", null),
		new RequestInfo(POST, "/members/password-reset/mail", RoleEnum.BUYER),
		new RequestInfo(PUT, "/members/password", RoleEnum.BUYER),
		new RequestInfo(DELETE, "/members/*", RoleEnum.BUYER),
		new RequestInfo(GET, "/members/*", RoleEnum.BUYER),
		new RequestInfo(GET, "/members/*/purchases", RoleEnum.BUYER),
		new RequestInfo(GET, "/members/*/sale", RoleEnum.SELLER),
		new RequestInfo(POST, "/members/*/points", RoleEnum.BUYER),

		// auth
		new RequestInfo(POST, "/auth/login", null),
		new RequestInfo(POST, "/auth/refresh", null),

		// ticketing
		new RequestInfo(GET, "/ticketings", null),
		new RequestInfo(GET, "/ticketings/*", null),
		new RequestInfo(POST, "/ticketings", RoleEnum.SELLER),
		new RequestInfo(PATCH, "/ticketings/*", RoleEnum.SELLER),
		new RequestInfo(DELETE, "/ticketings/*", RoleEnum.SELLER),

		// purchase
		new RequestInfo(POST, "/purchases", RoleEnum.BUYER),
		new RequestInfo(DELETE, "/purchases/*/tickets", RoleEnum.BUYER),

		// swagger
		new RequestInfo(GET, "/v3/api-docs/**", null),
		new RequestInfo(GET, "/swagger-ui.html", null),
		new RequestInfo(GET, "/swagger-ui/**", null)
	);

	private record RequestInfo(HttpMethod method, String pattern, RoleEnum minRole) {
	}
}
