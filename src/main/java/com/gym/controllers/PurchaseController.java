package com.gym.controllers;

import com.gym.dto.PurchaseDTO;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.services.PurchaseService;
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
@RequestMapping("/purchase")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @Operation(summary = "Traer todas las compras")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllPurchases() {
        try {
            List<PurchaseDTO> purchasesDTO = purchaseService.getAllPurchases();
            if (purchasesDTO.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body("There is no purchases yet");
            }
            return ResponseEntity.ok(purchasesDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve purchases: " + e.getMessage());
        }
    }

    @Operation(summary = "Traer una compra por ID")
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getPurchaseById(@PathVariable Long id) {
        try {
            PurchaseDTO purchaseDTO = purchaseService.getPurchaseById(id);
            return ResponseEntity.ok(purchaseDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Purchase with ID " + id + " not found");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve purchase with ID " + id + ": " + e.getMessage());
        }
    }

    @Operation(summary = "Crear una compra")
    @PostMapping("/create")
    public ResponseEntity<?> createPurchase(@Valid @RequestBody PurchaseDTO purchaseRequest) {
        try {
            PurchaseDTO purchaseResponse = purchaseService.createPurchase(purchaseRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(purchaseResponse);
        } catch (ValidationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create purchase: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar un detalle de compra")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePurchase(@PathVariable Long id, @Valid @RequestBody PurchaseDTO purchaseRequest) {
        purchaseRequest.setId(id);
        try {
            PurchaseDTO purchaseResponse = purchaseService.updatePurchase(purchaseRequest);
            return ResponseEntity.ok(purchaseResponse);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Purchase with ID " + id + " not found");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update purchase with ID " + id + ": " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar una compra por ID")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePurchaseById(@PathVariable Long id) {
        try {
            purchaseService.deletePurchaseById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Purchase with ID " + id + " has successfully deleted");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Purchase with ID " + id + " not found");
        }
    }
}
