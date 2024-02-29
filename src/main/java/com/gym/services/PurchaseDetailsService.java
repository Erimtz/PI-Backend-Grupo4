package com.gym.services;

import com.gym.entities.PurchaseDetails;
import com.gym.repositories.PurchaseDetailsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseDetailsService {

    private static final double PRICE_PER_UNIT = 10.0; // Precio base por unidad

    private static final double DISCOUNT_RATE = 0.1; // Tasa de descuento (10%)

    private PurchaseDetailsRepository purchaseDetailsRepository;

    public PurchaseDetailsService(PurchaseDetailsRepository purchaseDetailsRepository) {
        this.purchaseDetailsRepository = purchaseDetailsRepository;
    }

    public List<PurchaseDetails> getAllPurchaseDetails() {
        return (List<PurchaseDetails>) purchaseDetailsRepository.findAll();
    }

    // Método para obtener todos los detalles de la compra para una compra específica
    public List<PurchaseDetails> getPurchaseDetailsByPurchaseId(Long purchaseId) {
        return purchaseDetailsRepository.findByPurchaseId(purchaseId);
    }

    // Método para obtener todos los detalles de la compra para un producto específico
    public List<PurchaseDetails> getPurchaseDetailsByProductId(Long productId) {
        return purchaseDetailsRepository.findByProductId(productId);
    }


    public double calculatePrice(int quantity, boolean applyDiscount) {
        double totalPrice = PRICE_PER_UNIT * quantity; // Precio total sin descuento

        // Aplicar descuento por cantidad si corresponde y applyDiscount es verdadero
        if (applyDiscount) {
            double discountAmount = totalPrice * DISCOUNT_RATE; // Monto del descuento
            totalPrice -= discountAmount; // Restar el descuento al precio total
        }

        return totalPrice;
    }

}
