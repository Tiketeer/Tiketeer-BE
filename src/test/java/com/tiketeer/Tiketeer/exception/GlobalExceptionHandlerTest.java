package com.tiketeer.Tiketeer.exception;

import java.nio.charset.Charset;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiketeer.Tiketeer.configuration.SecurityDisableForTestConfig;
import com.tiketeer.Tiketeer.exception.code.CommonExceptionCode;
import com.tiketeer.Tiketeer.exception.code.ExceptionCode;
import com.tiketeer.Tiketeer.exception.code.MemberExceptionCode;
import com.tiketeer.Tiketeer.exception.code.TicketExceptionCode;
import com.tiketeer.Tiketeer.exception.code.TicketingExceptionCode;

@Import({SecurityDisableForTestConfig.class, DummyRestController.class})
@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {
	private final MockMvc mockMvc;
	private final ObjectMapper objectMapper;

	@Autowired
	public GlobalExceptionHandlerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
		this.mockMvc = mockMvc;
		this.objectMapper = objectMapper;
	}

	@Test
	@DisplayName("RuntimeException 발생 요청 준비 > 서버 측 요청 > INTERNAL_SERVER_ERROR 반환")
	void throwRuntimeException() throws Exception {
		throwException(CommonExceptionCode.INTERNAL_SERVER_ERROR);
	}

	@Test
	@DisplayName("DuplicatedEmailException 발생 요청 준비 > 서버 측 요청 > DUPLICATED_EMAIL 반환")
	void throwDuplicatedEmailException() throws Exception {
		throwException(MemberExceptionCode.DUPLICATED_EMAIL);
	}

	@Test
	@DisplayName("MemberNotFoundException 발생 요청 준비 > 서버 측 요청 > MEMBER_NOT_FOUND 반환")
	void throwMemberNotFoundException() throws Exception {
		throwException(MemberExceptionCode.MEMBER_NOT_FOUND);
	}

	@Test
	@DisplayName("TicketingNotFoundException 발생 요청 준비 > 서버 측 요청 > TICKETING_NOT_FOUND 반환")
	void throwTicketingNotFoundException() throws Exception {
		throwException(TicketingExceptionCode.TICKETING_NOT_FOUND);
	}

	@Test
	@DisplayName("TicketNotFoundException 발생 요청 준비 > 서버 측 요청 > TICKET_NOT_FOUND 반환")
	void throwTicketNotFoundException() throws Exception {
		throwException(TicketExceptionCode.TICKET_NOT_FOUND);
	}

	private void throwException(ExceptionCode targetExceptionCode) throws Exception {
		// given
		var dummyControllerPath = "/test/exception-handler/";
		var errorCodeName = targetExceptionCode.name();
		// when
		mockMvc.perform(MockMvcRequestBuilders.get(dummyControllerPath + errorCodeName))
			// then
			.andExpect(MockMvcResultMatchers.status().is(targetExceptionCode.getHttpStatus().value()))
			.andDo(result -> {
				String contentString = result.getResponse().getContentAsString(Charset.defaultCharset());
				ErrorResponse errorResponse = objectMapper.readValue(contentString, ErrorResponse.class);
				Assertions.assertThat(errorResponse.getCode()).isEqualTo(targetExceptionCode.name());
				Assertions.assertThat(errorResponse.getMessage()).isEqualTo(targetExceptionCode.getMessage());
			});
	}

}
