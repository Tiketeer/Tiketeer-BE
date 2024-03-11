package com.tiketeer.Tiketeer.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorResponse {
	private String code;
	private String message;

	@Builder
	public ErrorResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}
}
