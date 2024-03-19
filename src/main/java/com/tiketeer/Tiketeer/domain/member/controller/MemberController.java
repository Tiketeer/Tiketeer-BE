package com.tiketeer.Tiketeer.domain.member.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.auth.SecurityContextHelper;
import com.tiketeer.Tiketeer.domain.member.controller.dto.ChargePointRequestDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.ChargePointResponseDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.MemberRegisterRequestDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.MemberRegisterResponseDto;
import com.tiketeer.Tiketeer.domain.member.service.MemberPointService;
import com.tiketeer.Tiketeer.domain.member.service.MemberRegisterService;
import com.tiketeer.Tiketeer.domain.member.service.dto.MemberRegisterCommandDto;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/members")
public class MemberController {
	private final MemberRegisterService memberRegisterService;
	private final MemberPointService memberPointService;
	private final SecurityContextHelper securityContextHelper;

	@Autowired
	public MemberController(MemberRegisterService memberRegisterService, MemberPointService memberPointService,
		SecurityContextHelper securityContextHelper) {
		this.memberRegisterService = memberRegisterService;
		this.memberPointService = memberPointService;
		this.securityContextHelper = securityContextHelper;
	}

	@PostMapping("/register")
	public ApiResponse<MemberRegisterResponseDto> registerMember(
		final @Valid @RequestBody MemberRegisterRequestDto registerMemberDto) {
		return ApiResponse.wrap(
			MemberRegisterResponseDto.toDto(memberRegisterService.register(MemberRegisterCommandDto.builder()
				.email(registerMemberDto.getEmail())
				.isSeller(registerMemberDto.getIsSeller())
				.build()
			))
		);
	}

	// TODO: 이메일 전송 EP들의 Path 가독성 논의 후 Path 수정
	@PostMapping(path = "/{memberId}/password")
	public ResponseEntity sendPasswordChangeEmail(@PathVariable UUID memberId) {
		var email = securityContextHelper.getEmail();
		return ResponseEntity.ok().build();
	}

	@PostMapping(path = "/{memberId}/points")
	public ResponseEntity<ApiResponse<ChargePointResponseDto>> chargePoint(@PathVariable String memberId,
		@Valid @RequestBody ChargePointRequestDto request) {
		var email = securityContextHelper.getEmail();
		var totalPoint = memberPointService.chargePoint(request.convertToCommandDto(memberId, email)).getTotalPoint();
		var result = ChargePointResponseDto.builder().totalPoint(totalPoint).build();
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.wrap(result));
	}
}
