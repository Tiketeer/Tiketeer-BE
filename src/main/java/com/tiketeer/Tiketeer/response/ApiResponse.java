package com.tiketeer.Tiketeer.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiResponse<T> {
	private T data;

	@Builder
	public ApiResponse(T data) {
		this.data = data;
	}
}
