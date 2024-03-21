package com.tiketeer.Tiketeer.auth.constant;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PublicPaths {
	public static List<String> getPublicMemberPaths() {
		return List.of("/members/register");
	}

	public static List<String> getPublicAuthPaths() {
		return List.of("/auth/login", "/auth/refresh");
	}

	public static List<String> getPublicTicketingPaths() {
		return List.of("/ticketings", "/ticketings/*");
	}

	public static List<String> getSwaggerPaths() {
		return List.of("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**");
	}

	public static List<String> getSwaggerPathPrefixes() {
		return List.of("/v3/api-docs", "/swagger-ui.html", "/swagger-ui");
	}

	public static List<String> getAllPublicPaths() {
		return Stream.of(
				getSwaggerPaths(),
				getPublicMemberPaths(),
				getPublicAuthPaths(),
				getPublicTicketingPaths()
			).flatMap(List::stream)
			.collect(Collectors.toList());
	}

	public static List<String> appendApiPrefix(List<String> paths) {
		return paths.stream()
			.map(path -> "/api" + path)
			.collect(Collectors.toList());
	}

}
