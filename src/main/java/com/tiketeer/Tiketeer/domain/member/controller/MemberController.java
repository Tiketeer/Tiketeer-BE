package com.tiketeer.Tiketeer.domain.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@Autowired
	public MemberController(MemberRegisterService memberRegisterService, MemberPointService memberPointService) {
		this.memberRegisterService = memberRegisterService;
		this.memberPointService = memberPointService;
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

	@PostMapping(path = "/{memberId}/points")
	public ResponseEntity<ApiResponse<ChargePointResponseDto>> chargePoint(@PathVariable String memberId,
		@Valid @RequestBody ChargePointRequestDto request) {
		// TODO: JWT 구현이 완료되면 SecurityContext를 통해 가져오는 것으로 대체
		var email = "mock@mock.com";
		var totalPoint = memberPointService.chargePoint(request.convertToCommandDto(memberId, email)).getTotalPoint();
		var result = ChargePointResponseDto.builder().totalPoint(totalPoint).build();
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.wrap(result));
	}
}
