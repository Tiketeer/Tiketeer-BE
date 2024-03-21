package com.tiketeer.Tiketeer.domain.purchase.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiketeer.Tiketeer.domain.purchase.controller.dto.DeletePurchaseTicketsRequestDto;
import com.tiketeer.Tiketeer.domain.purchase.controller.dto.PostPurchaseRequestDto;
import com.tiketeer.Tiketeer.domain.purchase.controller.dto.PostPurchaseResponseDto;
import com.tiketeer.Tiketeer.domain.purchase.usecase.CreatePurchaseUseCase;
import com.tiketeer.Tiketeer.domain.purchase.usecase.DeletePurchaseTicketsUseCase;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {
	private final CreatePurchaseUseCase createPurchaseUseCase;
	private final DeletePurchaseTicketsUseCase deletePurchaseTicketsUseCase;

	@Autowired
	PurchaseController(CreatePurchaseUseCase createPurchaseUseCase,
		DeletePurchaseTicketsUseCase deletePurchaseTicketsUseCase) {
		this.createPurchaseUseCase = createPurchaseUseCase;
		this.deletePurchaseTicketsUseCase = deletePurchaseTicketsUseCase;
	}

	@PostMapping("/")
	public ResponseEntity<ApiResponse<PostPurchaseResponseDto>> postPurchase(
		@Valid @RequestBody PostPurchaseRequestDto request) {
		var memberEmail = "mock@mock.com";
		var result = createPurchaseUseCase.createPurchase(request.convertToDto(memberEmail));
		var responseBody = ApiResponse.wrap(PostPurchaseResponseDto.converFromDto(result));
		return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
	}

	@DeleteMapping("/{purchaseId}/tickets")
	public ResponseEntity deletePurchaseTickets(@PathVariable UUID purchaseId, @Valid @RequestBody
	DeletePurchaseTicketsRequestDto request) {
		var memberEmail = "mock@mock.com";
		deletePurchaseTicketsUseCase.deletePurchaseTickets(request.convertToDto(memberEmail, purchaseId));
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
