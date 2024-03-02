package com.gym.services;

import com.gym.dto.PurchaseRequestDTO;
import com.gym.entities.Account;
import com.gym.entities.Purchase;

import java.util.List;

public interface PurchaseService {

//    List<PurchaseRequestDTO> getAllPurchases();
//    PurchaseRequestDTO getPurchaseById(Long id);
    Purchase createPurchase(PurchaseRequestDTO purchaseDTO, String token);
//    PurchaseRequestDTO updatePurchase(PurchaseRequestDTO purchaseDTO);
//    void deletePurchaseById(Long id);
}
