package com.tiketeer.Tiketeer.auth.constant;

import java.util.List;

public class PublicPaths {
	public static List<String> getSwaggerPathPrefixes() {
		return List.of("/api/v3/api-docs", "/api/swagger-ui");
	}

	public static List<String> getMemberPaths() {
		return List.of("/api/auth/member", "/api/members/register");
	}

}
