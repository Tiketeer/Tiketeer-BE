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
import com.tiketeer.Tiketeer.domain.purchase.service.PurchaseService;
import com.tiketeer.Tiketeer.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {
	private final PurchaseService purchaseService;

	@Autowired
	PurchaseController(PurchaseService purchaseService) {
		this.purchaseService = purchaseService;
	}

	@PostMapping("/")
	public ResponseEntity<ApiResponse<PostPurchaseResponseDto>> postPurchase(
		@Valid @RequestBody PostPurchaseRequestDto request) {
		var memberEmail = "test@example.com";
		var result = purchaseService.createPurchase(request.convertToDto(memberEmail));
		var responseBody = ApiResponse.wrap(PostPurchaseResponseDto.converFromDto(result));
		return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
	}

	@DeleteMapping("/{purchaseId}/tickets")
	public ResponseEntity deletePurchaseTickets(@PathVariable UUID purchaseId, @Valid @RequestBody
	DeletePurchaseTicketsRequestDto request) {
		var memberEmail = "test@example.com";
		purchaseService.deletePurchaseTickets(request.convertToDto(memberEmail, purchaseId));
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
