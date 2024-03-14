package com.tiketeer.Tiketeer.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ResponseEntityInterceptor implements ResponseBodyAdvice<Object> {
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
		Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		if (!(body instanceof ResponseEntity<?> originalResponseEntity)) {
			return body;
		}
		HttpStatusCode httpStatus = originalResponseEntity.getStatusCode();
		Object originalBody = originalResponseEntity.getBody();

		// 예외의 경우엔 이미 GlobalExceptionHandler 레벨에서 처리하기 때문에 건너 뜀
		if (!httpStatus.is2xxSuccessful() || originalBody == null) {
			return body;
		}
		ApiResponse wrappedBody = ApiResponse.builder().data(originalBody).build();
		
		return new ResponseEntity<>(wrappedBody, originalResponseEntity.getHeaders(),
			originalResponseEntity.getStatusCode());
	}
}
