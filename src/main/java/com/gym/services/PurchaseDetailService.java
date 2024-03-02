package com.gym.services;

import com.gym.dto.PurchaseDetailDTO;
import com.gym.dto.PurchaseDetailRequestDTO;
import com.gym.entities.PurchaseDetail;

import java.util.List;

public interface PurchaseDetailService {

//    List<PurchaseDetailDTO> getAllPurchaseDetails();
//    PurchaseDetailDTO getPurchaseDetailById(Long id);
    PurchaseDetail createPurchaseDetail(PurchaseDetailRequestDTO purchaseDetailDTO);
//    PurchaseDetailDTO updatePurchaseDetail(PurchaseDetailDTO purchaseDetailDTO);
//    void deletePurchaseDetailById(Long id);
}
