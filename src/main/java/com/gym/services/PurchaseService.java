package com.gym.services;

import com.gym.dto.PurchaseDTO;
import com.gym.dto.PurchaseDetailDTO;

import java.util.List;

public interface PurchaseService {

    List<PurchaseDTO> getAllPurchases();
    PurchaseDTO getPurchaseById(Long id);
    PurchaseDTO createPurchase(PurchaseDTO purchaseDTO);
    PurchaseDTO updatePurchase(PurchaseDTO purchaseDTO);
    void deletePurchaseById(Long id);
}
