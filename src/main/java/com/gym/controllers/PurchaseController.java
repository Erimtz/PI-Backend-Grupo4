package com.gym.controllers;

import com.gym.dto.PurchaseRequestDTO;
import com.gym.entities.Account;
import com.gym.entities.Purchase;
import com.gym.services.AccountService;
import com.gym.services.PurchaseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<Purchase> createPurchase(@RequestBody PurchaseRequestDTO requestDTO, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            Purchase purchase = purchaseService.createPurchase(requestDTO, token);
            return new ResponseEntity<>(purchase, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}