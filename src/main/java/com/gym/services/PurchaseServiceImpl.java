package com.gym.services;

import com.gym.dto.request.PurchaseRequestDTO;
import com.gym.dto.response.CouponResponseDTO;
import com.gym.dto.response.PurchaseDetailResponseDTO;
import com.gym.dto.response.PurchaseResponseDTO;
import com.gym.entities.*;
import com.gym.repositories.ProductRepository;
import com.gym.repositories.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService{

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final AccountService accountService;
    private final StoreSubscriptionService storeSubscriptionService;

    public PurchaseResponseDTO createPurchase(PurchaseRequestDTO requestDTO, String token) {
        Account account = accountService.getAccountFromToken(token);
        if (account == null) {
            throw new IllegalArgumentException("No se pudo obtener la cuenta del usuario");
        }

        Purchase purchase = new Purchase();
        purchase.setPurchaseDate(LocalDate.now());
        purchase.setAccount(account);

        StoreSubscription storeSubscription = storeSubscriptionService.convertToEntity(storeSubscriptionService.getStoreSubscriptionById(requestDTO.getStoreSubscriptionId()));
        purchase.setStoreSubscription(storeSubscription);

        List<PurchaseDetail> purchaseDetails = requestDTO.getPurchaseDetails().stream()
                .map(detailDTO -> {
                    Product product = productRepository.findById(detailDTO.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + detailDTO.getProductId()));

                    PurchaseDetail detail = new PurchaseDetail();
                    detail.setProduct(product);
                    detail.setQuantity(detailDTO.getQuantity());
                    return detail;
                })
                .collect(Collectors.toList());

        purchase.setPurchaseDetails(purchaseDetails);
        purchase.setCouponsApplied(requestDTO.getCouponsApplied());
        purchase = purchaseRepository.save(purchase);

        List<PurchaseDetailResponseDTO> detailDTOs = purchaseDetails.stream()
                .map(detail -> {
                    PurchaseDetailResponseDTO detailDTO = new PurchaseDetailResponseDTO();
                    detailDTO.setProductName(detail.getProduct().getName());
                    detailDTO.setQuantity(detail.getQuantity());
                    detailDTO.setSubtotal(calculateSubtotal(detail));
                    return detailDTO;
                })
                .collect(Collectors.toList());

        List<CouponResponseDTO> couponsResponseDTO = requestDTO.getCouponsApplied().stream()
                .map(coupon -> new CouponResponseDTO(coupon.getId(), coupon.getAmount()))
                .collect(Collectors.toList());

        Double total = detailDTOs.stream().mapToDouble(PurchaseDetailResponseDTO::getSubtotal).sum();
        Double discount = couponsResponseDTO.stream().mapToDouble(CouponResponseDTO::getAmount).sum();
        Double totalAfterDiscounts = total - discount;

        return new PurchaseResponseDTO(detailDTOs, purchase.getStoreSubscription().getPrice(), total, couponsResponseDTO, discount, totalAfterDiscounts);
    }

    private Double calculateSubtotal(PurchaseDetail purchaseDetail) {
        if (purchaseDetail.getProduct() != null && purchaseDetail.getProduct().getPrice() != null) {
            return purchaseDetail.getProduct().getPrice().doubleValue() * purchaseDetail.getQuantity();
        } else {
            // Manejo del caso en que el precio del producto es null
            throw new IllegalArgumentException("Price of the product is null");
        }
    }

    private Double calculateTotalAfterDiscounts(Double total, List<Coupon> couponsApplied, StoreSubscription storeSubscription) {
        Double totalAfterCoupons = total - couponsApplied.stream().mapToDouble(Coupon::getAmount).sum();
        return totalAfterCoupons - storeSubscription.getPrice();
    }
}