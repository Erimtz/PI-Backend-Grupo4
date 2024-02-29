package com.gym.controllers;

import com.gym.entities.PurchaseDetails;
import com.gym.services.PurchaseDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase-details")
@CrossOrigin(origins = "*")
public class PurchaseDetailsController {

    private PurchaseDetailsService purchaseDetailsService;

    public PurchaseDetailsController(PurchaseDetailsService purchaseDetailsService) {
        this.purchaseDetailsService = purchaseDetailsService;
    }

    // Endpoint para obtener todos los detalles de la compra
    @GetMapping
    public ResponseEntity<List<PurchaseDetails>> getAllPurchaseDetails() {
        List<PurchaseDetails> purchaseDetailsList = purchaseDetailsService.getAllPurchaseDetails();
        return new ResponseEntity<>(purchaseDetailsList, HttpStatus.OK);
    }

    // Endpoint para obtener todos los detalles de la compra para una compra específica
    @GetMapping("/by-purchase/{purchaseId}")
    public ResponseEntity<List<PurchaseDetails>> getPurchaseDetailsByPurchaseId(@PathVariable Long purchaseId) {
        List<PurchaseDetails> purchaseDetailsList = purchaseDetailsService.getPurchaseDetailsByPurchaseId(purchaseId);
        return new ResponseEntity<>(purchaseDetailsList, HttpStatus.OK);
    }

    // Endpoint para obtener todos los detalles de la compra para un producto específico
    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<PurchaseDetails>> getPurchaseDetailsByProductId(@PathVariable Long productId) {
        List<PurchaseDetails> purchaseDetailsList = purchaseDetailsService.getPurchaseDetailsByProductId(productId);
        return new ResponseEntity<>(purchaseDetailsList, HttpStatus.OK);
    }

    @GetMapping("/calculatePrice")
    public double calculatePrice(@RequestParam int quantity, @RequestParam(required = false, defaultValue = "false") boolean applyDiscount) {
        return purchaseDetailsService.calculatePrice(quantity, applyDiscount);
    }
}
