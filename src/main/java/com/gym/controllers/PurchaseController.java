package com.gym.controllers;

import com.gym.dto.request.PurchaseRequestDTO;
import com.gym.dto.response.PurchaseResponseDTO;
import com.gym.exceptions.*;
import com.gym.security.configuration.utils.AccountTokenUtils;
import com.gym.services.PurchaseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final AccountTokenUtils accountTokenUtils;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> createPurchase(@RequestBody PurchaseRequestDTO requestDTO, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            PurchaseResponseDTO purchaseResponseDTO = purchaseService.createPurchase(requestDTO, token);
            return new ResponseEntity<>(purchaseResponseDTO, HttpStatus.CREATED);
        } catch (NotEnoughStockException ex) {
            return new ResponseEntity<>("Not enough stock available", HttpStatus.BAD_REQUEST);
        } catch (InsufficientCreditException ex) {
            return new ResponseEntity<>("Insufficient credit balance", HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException ex) {
            return new ResponseEntity<>("Coupon has already been spent", HttpStatus.BAD_REQUEST);
        } catch (CouponDiscountExceededException ex) {
            return new ResponseEntity<>("Total coupon discount exceeds the maximum allowed", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<PurchaseResponseDTO>> getPurchasesByAccount(@PathVariable Long accountId, HttpServletRequest request) {
        try {
            boolean hasAccess = accountTokenUtils.hasAccessToAccount(request, accountId);
            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            List<PurchaseResponseDTO> purchaseResponseDTOs = purchaseService.getPurchasesByAccount(accountId, request);
            return ResponseEntity.ok(purchaseResponseDTOs);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<PurchaseResponseDTO>> getAllPurchases(HttpServletRequest request) {
        try {
            List<PurchaseResponseDTO> purchaseResponseDTOs = purchaseService.getAllPurchases(request);
            return ResponseEntity.ok(purchaseResponseDTOs);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<PurchaseResponseDTO> getPurchaseById(@PathVariable Long id, HttpServletRequest request) {
        try {
            PurchaseResponseDTO purchaseResponseDTO = purchaseService.getPurchaseById(id, request);
            return ResponseEntity.ok(purchaseResponseDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", e);
        }
    }
}