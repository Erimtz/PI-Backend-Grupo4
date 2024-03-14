package com.gym.services;

import com.gym.dto.CouponResponseDTO;
import com.gym.dto.request.DateRangeDTO;
import com.gym.dto.request.PurchaseRequestDTO;
import com.gym.dto.response.PurchaseResponseDTO;
import com.gym.entities.Purchase;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseService {

//    List<PurchaseRequestDTO> getAllPurchases();
//    PurchaseRequestDTO getPurchaseById(Long id);
    PurchaseResponseDTO createPurchase(PurchaseRequestDTO purchaseDTO, String token);
    Double calculateTotal(Purchase purchase);

    List<PurchaseResponseDTO> getPurchasesByAccount(Long accountId, HttpServletRequest request);
//    PurchaseRequestDTO updatePurchase(PurchaseRequestDTO purchaseDTO);
//    void deletePurchaseById(Long id);
    List<PurchaseResponseDTO> getAllPurchases(HttpServletRequest request);
    PurchaseResponseDTO getPurchaseById(Long id, HttpServletRequest request);
//    Double getSumTotalAfterDiscountsByDateRange(DateRangeDTO dateRangeDTO);

    List<PurchaseResponseDTO> getAllPurchasesByDateRange(DateRangeDTO dateRangeDTO);
    Double getTotalAfterDiscountsSumByDateRange(DateRangeDTO dateRangeDTO);
    Long getPurchasesCountByDateRange(DateRangeDTO dateRangeDTO);
    Double getPurchasesAverageByDateRange(DateRangeDTO dateRangeDTO);
}
