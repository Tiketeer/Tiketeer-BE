package com.tiketeer.Tiketeer.domain.member.dto;

import java.util.UUID;

import com.tiketeer.Tiketeer.domain.member.Member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDto {
	@Getter
	@NoArgsConstructor(force = true)
	public static class RegisterMemberDto {
		@Email(message = "올바른 이메일 형식이 아닙니다.")
		@NotEmpty(message = "이메일은 비워둘 수 없습니다.")
		private final String email;

		@NotNull(message = "비밀번호는 비워둘 수 없습니다.")
		private final String password;

		@NotNull(message = "판매자 여부를 선택해야 합니다.")
		private final Boolean isSeller;

		@Builder
		public RegisterMemberDto(String email, String password, Boolean isSeller) {
			this.email = email;
			this.password = password;
			this.isSeller = isSeller;
		}
	}

	@Getter
	@NoArgsConstructor(force = true)
	public static class RegisterMemberResponseDto {
		private final UUID memberId;

		@Builder
		public RegisterMemberResponseDto(UUID memberId) {
			this.memberId = memberId;
		}

		public static RegisterMemberResponseDto toDto(Member member) {
			return new RegisterMemberResponseDto(member.getId());
		}
	}
}
