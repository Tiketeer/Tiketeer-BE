package com.tiketeer.Tiketeer.domain.member.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.member.controller.dto.ChargePointRequestDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.ChargePointResponseDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.GetMemberTicketingSalesResponseDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.MemberRegisterRequestDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.MemberRegisterResponseDto;
import com.tiketeer.Tiketeer.domain.member.service.MemberPointService;
import com.tiketeer.Tiketeer.domain.member.service.MemberRegisterService;
import com.tiketeer.Tiketeer.domain.member.service.MemberTicketingService;
import com.tiketeer.Tiketeer.domain.member.service.dto.GetMemberTicketingSalesCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.MemberRegisterCommandDto;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/members")
public class MemberController {
	private final MemberRegisterService memberRegisterService;
	private final MemberTicketingService memberTicketingService;
	private final MemberPointService memberPointService;

	@Autowired
	public MemberController(MemberRegisterService memberRegisterService, MemberPointService memberPointService,
		MemberTicketingService memberTicketingService) {
		this.memberRegisterService = memberRegisterService;
		this.memberPointService = memberPointService;
		this.memberTicketingService = memberTicketingService;
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

	@GetMapping("/{memberId}/sale")
	public ResponseEntity<ApiResponse<List<GetMemberTicketingSalesResponseDto>>> getMemberTicketingSales(
		@PathVariable UUID memberId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = (String)authentication.getPrincipal();
		var result = memberTicketingService.getMemberTicketingSales(
			new GetMemberTicketingSalesCommandDto(memberId, email));
		var response = result.stream().map(GetMemberTicketingSalesResponseDto::convertFromResult).toList();
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.wrap(response));
	}

}
