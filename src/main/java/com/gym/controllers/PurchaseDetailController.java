package com.gym.controllers;

import com.gym.dto.PurchaseDetailRequestDTO;
import com.gym.entities.PurchaseDetail;
import com.gym.services.PurchaseDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchase-details")
@RequiredArgsConstructor
public class PurchaseDetailController {

    private final PurchaseDetailService purchaseDetailService;

    @PostMapping
    public ResponseEntity<PurchaseDetail> createPurchaseDetail(@RequestBody PurchaseDetailRequestDTO requestDTO) {
        if (requestDTO.getProductId() == null) {
            throw new IllegalArgumentException("productId cannot be null");
        }

        PurchaseDetail purchaseDetail = purchaseDetailService.createPurchaseDetail(requestDTO);
        return new ResponseEntity<>(purchaseDetail, HttpStatus.CREATED);
    }

    // Otros métodos del controlador, si es necesario
}