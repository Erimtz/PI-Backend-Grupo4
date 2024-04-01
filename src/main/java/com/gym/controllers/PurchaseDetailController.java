package com.gym.controllers;

import com.gym.dto.request.PurchaseDetailRequestDTO;
import com.gym.entities.PurchaseDetail;
import com.gym.services.PurchaseDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/purchase-details")
public class PurchaseDetailController {

    private final PurchaseDetailService purchaseDetailService;

    @Operation(summary = "Create purchase detail")
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Purchase detail created successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDetail.class))
            })
    })
    public ResponseEntity<PurchaseDetail> createPurchaseDetail(@RequestBody PurchaseDetailRequestDTO requestDTO) {
        if (requestDTO.getProductId() == null) {
            throw new IllegalArgumentException("productId cannot be null");
        }

        PurchaseDetail purchaseDetail = purchaseDetailService.createPurchaseDetail(requestDTO);
        return new ResponseEntity<>(purchaseDetail, HttpStatus.CREATED);
    }
}