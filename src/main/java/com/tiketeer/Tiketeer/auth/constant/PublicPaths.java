package com.tiketeer.Tiketeer.auth.constant;

import java.util.List;
import java.util.stream.Collectors;

public class PublicPaths {
	public static List<String> getSwaggerPathPrefixes() {
		return List.of("/v3/api-docs", "/swagger-ui");
	}

	public static List<String> getMemberPaths() {
		return List.of("/auth/login", "/members/register");
	}

	public static List<String> getSwaggerPaths() {
		return List.of("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**");
	}

	public static List<String> appendApiPrefix(List<String> paths) {
		return paths.stream()
			.map(path -> "/api" + path)
			.collect(Collectors.toList());
	}

}
