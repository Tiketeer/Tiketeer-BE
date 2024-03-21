package com.tiketeer.Tiketeer.domain.member.controller;

import com.tiketeer.Tiketeer.domain.member.usecase.GetMemberUseCase;
import com.tiketeer.Tiketeer.domain.member.usecase.MemberChargePointUseCase;
import com.tiketeer.Tiketeer.domain.member.usecase.MemberRegisterUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.member.controller.dto.ChargePointRequestDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.ChargePointResponseDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.GetMemberResponseDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.MemberRegisterRequestDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.MemberRegisterResponseDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.GetMemberCommandDto;
import com.tiketeer.Tiketeer.domain.member.service.dto.MemberRegisterCommandDto;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/members")
public class MemberController {
	private final MemberRegisterUseCase memberRegisterUseCase;
	private final MemberChargePointUseCase memberChargePointUseCase;

	private final GetMemberUseCase getMemberUseCase;

	@Autowired
	public MemberController(MemberRegisterUseCase memberRegisterUseCase, MemberChargePointUseCase memberChargePointUseCase, GetMemberUseCase getMemberUseCase) {
        this.memberRegisterUseCase = memberRegisterUseCase;
        this.memberChargePointUseCase = memberChargePointUseCase;
        this.getMemberUseCase = getMemberUseCase;
	}

	@PostMapping("/register")
	public ApiResponse<MemberRegisterResponseDto> registerMember(
		final @Valid @RequestBody MemberRegisterRequestDto registerMemberDto) {
		return ApiResponse.wrap(
			MemberRegisterResponseDto.toDto(memberRegisterUseCase.register(MemberRegisterCommandDto.builder()
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
		var totalPoint = memberChargePointUseCase.chargePoint(request.convertToCommandDto(memberId, email)).getTotalPoint();
		var result = ChargePointResponseDto.builder().totalPoint(totalPoint).build();
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.wrap(result));
	}

	@GetMapping("/")
	public ResponseEntity<ApiResponse<GetMemberResponseDto>> getMember() {
		var email = "mock@mock.com";
		var result = getMemberUseCase.get(GetMemberCommandDto.builder().memberEmail(email).build());
		var responseBody = ApiResponse.wrap(GetMemberResponseDto.convertFromDto(result));
		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
	}
}
