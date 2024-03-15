package com.tiketeer.Tiketeer.domain.member.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.member.application.MemberRegisterService;
import com.tiketeer.Tiketeer.domain.member.dto.MemberDto.RegisterMemberDto;
import com.tiketeer.Tiketeer.domain.member.dto.MemberDto.RegisterMemberResponseDto;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/members")
public class MemberController {
	private final MemberRegisterService memberRegisterService;

	@Autowired
	public MemberController(MemberRegisterService memberRegisterService) {
		this.memberRegisterService = memberRegisterService;
	}

	@PostMapping("/register")
	public ApiResponse<RegisterMemberResponseDto> registerMember(
		final @Valid @RequestBody RegisterMemberDto registerMemberDto) {
		return ApiResponse.wrap(memberRegisterService.register(registerMemberDto));
	}
}
