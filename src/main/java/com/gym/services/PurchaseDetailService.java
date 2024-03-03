package com.gym.services;

import com.gym.dto.request.PurchaseDetailRequestDTO;
import com.gym.entities.PurchaseDetail;

public interface PurchaseDetailService {

//    List<PurchaseDetailDTO> getAllPurchaseDetails();
//    PurchaseDetailDTO getPurchaseDetailById(Long id);
    PurchaseDetail createPurchaseDetail(PurchaseDetailRequestDTO purchaseDetailDTO);
//    PurchaseDetailDTO updatePurchaseDetail(PurchaseDetailDTO purchaseDetailDTO);
//    void deletePurchaseDetailById(Long id);
}
