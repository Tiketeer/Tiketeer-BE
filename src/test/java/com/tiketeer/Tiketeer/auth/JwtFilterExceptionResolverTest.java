package com.tiketeer.Tiketeer.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiketeer.Tiketeer.exception.ErrorResponse;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;

@SpringBootTest
class JwtFilterExceptionResolverTest {

	@Autowired
	JwtFilterExceptionResolver jwtFilterExceptionResolver;

	@Test
	void setResponse() throws IOException {
		HttpServletResponse response = mock(HttpServletResponse.class);
		JwtException exception = new JwtException("invalid jwt");

		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(printWriter);

		jwtFilterExceptionResolver.setResponse(response, exception);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

		String responseContent = stringWriter.toString();

		String expectedJson = new ObjectMapper().writeValueAsString(
			new ErrorResponse(HttpStatus.UNAUTHORIZED.name(), "invalid jwt")
		);

		assertThat(expectedJson).isEqualTo(responseContent);
	}
}