package com.tiketeer.Tiketeer.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiResponse<T> {
	private T data;

	private ApiResponse(T data) {
		this.data = data;
	}

	public static <T> ApiResponse wrap(T data) {
		return new ApiResponse(data);
	}
}
