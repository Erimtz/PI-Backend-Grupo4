package com.gym.services;

import com.gym.dto.request.PurchaseRequestDTO;
import com.gym.dto.response.PurchaseResponseDTO;
import com.gym.entities.Purchase;

public interface PurchaseService {

//    List<PurchaseRequestDTO> getAllPurchases();
//    PurchaseRequestDTO getPurchaseById(Long id);
    PurchaseResponseDTO createPurchase(PurchaseRequestDTO purchaseDTO, String token);
    Double calculateTotal(Purchase purchase);
//    PurchaseRequestDTO updatePurchase(PurchaseRequestDTO purchaseDTO);
//    void deletePurchaseById(Long id);
}
