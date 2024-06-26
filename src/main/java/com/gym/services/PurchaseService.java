package com.gym.services;

import com.gym.dto.CouponResponseDTO;
import com.gym.dto.request.DateRangeDTO;
import com.gym.dto.request.PurchaseRequestDTO;
import com.gym.dto.response.CategorySalesResponseDTO;
import com.gym.dto.response.ProductAmountResponseDTO;
import com.gym.dto.response.ProductSalesResponseDTO;
import com.gym.dto.response.PurchaseResponseDTO;
import com.gym.entities.Product;
import com.gym.entities.Purchase;

import java.time.LocalDate;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;


public interface PurchaseService {

//    List<PurchaseRequestDTO> getAllPurchases();
//    PurchaseRequestDTO getPurchaseById(Long id);
    PurchaseResponseDTO createPurchase(PurchaseRequestDTO purchaseDTO, String token);
    Double calculateTotal(Purchase purchase);

    List<PurchaseResponseDTO> getPurchasesByAccount(Long accountId, HttpServletRequest request);

    List<CategorySalesResponseDTO> calculateSalesByCategory(DateRangeDTO dateRangeDTO);
//    PurchaseRequestDTO updatePurchase(PurchaseRequestDTO purchaseDTO);
//    void deletePurchaseById(Long id);
    List<PurchaseResponseDTO> getAllPurchases(HttpServletRequest request);
    PurchaseResponseDTO getPurchaseById(Long id, HttpServletRequest request);
//    Double getSumTotalAfterDiscountsByDateRange(DateRangeDTO dateRangeDTO);

    List<PurchaseResponseDTO> getAllPurchasesByDateRange(DateRangeDTO dateRangeDTO);
    Double getTotalAfterDiscountsSumByDateRange(DateRangeDTO dateRangeDTO);
    Long getPurchasesCountByDateRange(DateRangeDTO dateRangeDTO);
    Double getPurchasesAverageByDateRange(DateRangeDTO dateRangeDTO);
    PurchaseResponseDTO buildPurchaseResponse(Purchase purchase);
    Double calculateAveragePurchaseAmountPerUser();
    List<ProductSalesResponseDTO> getUnitsSoldByProduct();
    List<ProductAmountResponseDTO> getSalesByProduct();
//    Map<Product, Double> calculateTotalSalesByProduct();
}
