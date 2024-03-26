package com.tiketeer.Tiketeer.domain.member.annotation;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PasswordValidatorTest {

	private final PasswordValidator validator = new PasswordValidator();

	@Test
	void isValidSuccess() {
		String validPassword = "1q2w3e4r@@q";
		assertThat(validator.isValid(validPassword, null)).isTrue();
	}

	@Test
	void isValidFailNoSpecialCharacters() {
		String inValidPassword = "1q2w3e4rq";
		assertThat(validator.isValid(inValidPassword, null)).isFalse();
	}

	@Test
	void isValidFailNoDigit() {
		String inValidPassword = "qwerty@@qw";
		assertThat(validator.isValid(inValidPassword, null)).isFalse();
	}

	@Test
	void isValidFailTooShort() {
		String inValidPassword = "1q2w@q";
		assertThat(validator.isValid(inValidPassword, null)).isFalse();
	}

	@Test
	void isValidFailTooLong() {
		String inValidPassword = "1q2w@q1q2w@q1q2w@q";
		assertThat(validator.isValid(inValidPassword, null)).isFalse();
	}

}