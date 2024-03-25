package com.tiketeer.Tiketeer.domain.member.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.auth.SecurityContextHelper;
import com.tiketeer.Tiketeer.domain.member.controller.dto.ChargePointRequestDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.ChargePointResponseDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.GetMemberPurchasesResponseDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.GetMemberResponseDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.GetMemberTicketingSalesResponseDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.MemberRegisterRequestDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.MemberRegisterResponseDto;
import com.tiketeer.Tiketeer.domain.member.controller.dto.ResetPasswordRequestDto;
import com.tiketeer.Tiketeer.domain.member.usecase.ChargeMemberPointUseCase;
import com.tiketeer.Tiketeer.domain.member.usecase.GetMemberPurchasesUseCase;
import com.tiketeer.Tiketeer.domain.member.usecase.GetMemberTicketingSalesUseCase;
import com.tiketeer.Tiketeer.domain.member.usecase.GetMemberUseCase;
import com.tiketeer.Tiketeer.domain.member.usecase.MemberRegisterUseCase;
import com.tiketeer.Tiketeer.domain.member.usecase.ResetPasswordUseCase;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.GetMemberCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.GetMemberPurchasesCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.GetMemberTicketingSalesCommandDto;
import com.tiketeer.Tiketeer.domain.member.usecase.dto.MemberRegisterCommandDto;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/members")
public class MemberController {
	private final MemberRegisterUseCase memberRegisterUseCase;
	private final ChargeMemberPointUseCase chargeMemberPointUseCase;

	private final GetMemberTicketingSalesUseCase getMemberTicketingSalesUseCase;

	private final GetMemberUseCase getMemberUseCase;
	private final GetMemberPurchasesUseCase getMemberPurchasesUseCase;

	private final ResetPasswordUseCase resetPasswordUseCase;
	private final SecurityContextHelper securityContextHelper;

	@Autowired
	public MemberController(MemberRegisterUseCase memberRegisterUseCase,
		ChargeMemberPointUseCase chargeMemberPointUseCase, ResetPasswordUseCase resetPasswordUseCase,
		GetMemberTicketingSalesUseCase getMemberTicketingSalesUseCase,
		GetMemberUseCase getMemberUseCase, GetMemberPurchasesUseCase getMemberPurchasesUseCase,
		SecurityContextHelper securityContextHelper) {
		this.memberRegisterUseCase = memberRegisterUseCase;
		this.chargeMemberPointUseCase = chargeMemberPointUseCase;
		this.getMemberTicketingSalesUseCase = getMemberTicketingSalesUseCase;
		this.getMemberUseCase = getMemberUseCase;
		this.resetPasswordUseCase = resetPasswordUseCase;
		this.getMemberPurchasesUseCase = getMemberPurchasesUseCase;
		this.securityContextHelper = securityContextHelper;
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
		var email = securityContextHelper.getEmailInToken();
		var totalPoint = chargeMemberPointUseCase.chargePoint(request.convertToCommandDto(memberId, email))
			.getTotalPoint();
		var result = ChargePointResponseDto.builder().totalPoint(totalPoint).build();
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.wrap(result));
	}

	@GetMapping("/{memberId}/purchases")
	public ResponseEntity<ApiResponse<List<GetMemberPurchasesResponseDto>>> getMemberPurchases(
		@PathVariable UUID memberId) {
		var email = securityContextHelper.getEmailInToken();
		var results = getMemberPurchasesUseCase.getMemberPurchases(
			GetMemberPurchasesCommandDto.builder().memberEmail(email).build());
		var responseBody = ApiResponse.wrap(
			results.stream().map(GetMemberPurchasesResponseDto::convertFromDto).toList());
		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
	}

	@GetMapping("/{memberId}/sale")
	public ResponseEntity<ApiResponse<List<GetMemberTicketingSalesResponseDto>>> getMemberTicketingSales(
		@PathVariable UUID memberId) {
		var email = securityContextHelper.getEmailInToken();
		var result = getMemberTicketingSalesUseCase.getMemberTicketingSales(
			new GetMemberTicketingSalesCommandDto(memberId, email));
		var response = result.stream().map(GetMemberTicketingSalesResponseDto::convertFromResult).toList();
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.wrap(response));
	}

	@PutMapping("/password")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
		resetPasswordUseCase.resetPassword(request.toCommand());
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping()
	public ResponseEntity<ApiResponse<GetMemberResponseDto>> getMember() {
		var email = securityContextHelper.getEmailInToken();
		var result = getMemberUseCase.get(GetMemberCommandDto.builder().memberEmail(email).build());
		var responseBody = ApiResponse.wrap(GetMemberResponseDto.convertFromDto(result));
		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
	}
}
