package com.gym.controllers;

import com.gym.dto.CategoryDTO;
import com.gym.dto.PurchaseDetailDTO;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.services.PurchaseDetailService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/purchase/detail")
public class PurchaseDetailController {

    private final PurchaseDetailService purchaseDetailService;

    @Autowired
    public PurchaseDetailController(PurchaseDetailService purchaseDetailService) {
        this.purchaseDetailService = purchaseDetailService;
    }

    @Operation(summary = "Traer todos los detalles de compra")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllPurchaseDetails() {
        try {
            List<PurchaseDetailDTO> purchaseDetailsDTO = purchaseDetailService.getAllPurchaseDetails();
            if (purchaseDetailsDTO.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body("There is no purchases details yet");
            }
            return ResponseEntity.ok(purchaseDetailsDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve purchases details: " + e.getMessage());
        }
    }

    @Operation(summary = "Traer el detalle de la compra por ID")
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getPurchaseDetailById(@PathVariable Long id) {
        try {
            PurchaseDetailDTO purchaseDetailDTO = purchaseDetailService.getPurchaseDetailById(id);
            return ResponseEntity.ok(purchaseDetailDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Purchase details with ID " + id + " not found");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve purchase details with ID " + id + ": " + e.getMessage());
        }
    }

    @Operation(summary = "Crear un detalle de compra")
    @PostMapping("/create")
    public ResponseEntity<?> createPurchaseDetail(@Valid @RequestBody PurchaseDetailDTO purchaseDetailRequest) {
        try {
            PurchaseDetailDTO purchaseDetailResponse = purchaseDetailService.createPurchaseDetail(purchaseDetailRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(purchaseDetailResponse);
        } catch (ValidationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create purchase details: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar un detalle de compra")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePurchaseDetail(@PathVariable Long id, @Valid @RequestBody PurchaseDetailDTO purchaseDetailRequest) {
        purchaseDetailRequest.setId(id);
        try {
            PurchaseDetailDTO purchaseDetailResponse = purchaseDetailService.updatePurchaseDetail(purchaseDetailRequest);
            return ResponseEntity.ok(purchaseDetailResponse);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Purchase details with ID " + id + " not found");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update purchase details with ID " + id + ": " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar un detalle e compra por ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePurchaseDetailById(@PathVariable Long id) {
        try {
            purchaseDetailService.deletePurchaseDetailById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Purchase details with ID " + id + " has successfully deleted");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Purchase details with ID " + id + " not found");
        }
    }
}

