package com.gym.services;

import com.gym.dto.PurchaseDetailDTO;
import com.gym.entities.PurchaseDetail;

import java.util.List;

public interface PurchaseDetailService {

    List<PurchaseDetailDTO> getAllPurchaseDetails();
    PurchaseDetailDTO getPurchaseDetailById(Long id);
    PurchaseDetailDTO createPurchaseDetail(PurchaseDetailDTO purchaseDetailDTO);
    PurchaseDetailDTO updatePurchaseDetail(PurchaseDetailDTO purchaseDetailDTO);
    void deletePurchaseDetailById(Long id);
}
