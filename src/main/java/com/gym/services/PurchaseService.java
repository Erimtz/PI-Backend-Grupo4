package com.gym.services;

import com.gym.dto.request.PurchaseRequestDTO;
import com.gym.dto.response.PurchaseResponseDTO;

public interface PurchaseService {

//    List<PurchaseRequestDTO> getAllPurchases();
//    PurchaseRequestDTO getPurchaseById(Long id);
    PurchaseResponseDTO createPurchase(PurchaseRequestDTO purchaseDTO, String token);
//    PurchaseRequestDTO updatePurchase(PurchaseRequestDTO purchaseDTO);
//    void deletePurchaseById(Long id);
}
