package com.gym.controllers;

import com.gym.dto.request.PurchaseRequestDTO;
import com.gym.dto.response.PurchaseResponseDTO;
import com.gym.exceptions.CouponDiscountExceededException;
import com.gym.exceptions.InsufficientCreditException;
import com.gym.exceptions.NotEnoughStockException;
import com.gym.security.entities.UserEntity;
import com.gym.services.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Crear compra")
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Compra creada con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "500", description = "Error de respuesta",content =
            @Content),
            @ApiResponse(responseCode = "400", description = "Error de parametros",content =
            @Content),

    })
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
}