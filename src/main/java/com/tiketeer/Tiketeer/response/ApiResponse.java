package com.tiketeer.Tiketeer.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiResponse<T> {
	private final T data;

	private ApiResponse(T data) {
		this.data = data;
	}

	public static <T> ApiResponse<T> wrap(T data) {
		return new ApiResponse<>(data);
	}
}
