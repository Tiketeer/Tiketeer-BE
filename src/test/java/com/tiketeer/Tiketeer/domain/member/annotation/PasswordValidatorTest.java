package com.tiketeer.Tiketeer.domain.member.annotation;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordValidatorTest {

	private final PasswordValidator validator = new PasswordValidator();

	@Test
	@DisplayName("유효한 비밀번호 > 검증 > 성공")
	void isValidSuccess() {
		//given
		String validPassword = "1q2w3e4r@@q";
		//when - then
		assertThat(validator.isValid(validPassword, null)).isTrue();
	}

	@Test
	@DisplayName("특수문자가 없는 비밀번호 > 검증 > 실패")
	void isValidFailNoSpecialCharacters() {
		//given
		String inValidPassword = "1q2w3e4rq";
		//when - then
		assertThat(validator.isValid(inValidPassword, null)).isFalse();
	}

	@Test
	@DisplayName("숫자가 없는 비밀번호 > 검증 > 실패")
	void isValidFailNoDigit() {
		//given
		String inValidPassword = "qwerty@@qw";
		//when - then
		assertThat(validator.isValid(inValidPassword, null)).isFalse();
	}

	@Test
	@DisplayName("너무 짧은 비밀번호 > 검증 > 실패")
	void isValidFailTooShort() {
		//given
		String inValidPassword = "1q2w@q";
		//when - then
		assertThat(validator.isValid(inValidPassword, null)).isFalse();
	}

	@Test
	@DisplayName("너무 긴 비밀번호 > 검증 > 실패")
	void isValidFailTooLong() {
		//given
		String inValidPassword = "1q2w@q1q2w@q1q2w@q";
		//when - then
		assertThat(validator.isValid(inValidPassword, null)).isFalse();
	}

}