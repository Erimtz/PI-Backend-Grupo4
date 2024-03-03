package com.gym.services;

import com.gym.dto.CreateCouponDTO;
import com.gym.dto.CreateStoreSubscriptionDTO;
import com.gym.dto.ResponseStoreSubscription;
import com.gym.dto.request.PurchaseDetailRequestDTO;
import com.gym.dto.request.PurchaseRequestDTO;
import com.gym.dto.response.CouponResponseDTO;
import com.gym.dto.response.PurchaseDetailResponseDTO;
import com.gym.dto.response.PurchaseResponseDTO;
import com.gym.entities.*;
import com.gym.repositories.ProductRepository;
import com.gym.repositories.PurchaseRepository;
import com.gym.services.ale.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService{

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
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

        Long storeSubscriptionId = requestDTO.getStoreSubscriptionId();
        List<PurchaseDetailRequestDTO> purchaseDetailDTOs = requestDTO.getPurchaseDetails();

        if (storeSubscriptionId == null && (purchaseDetailDTOs == null || purchaseDetailDTOs.isEmpty())) {
            throw new IllegalArgumentException("Debe proporcionar al menos una suscripción a la tienda o un detalle de compra");
        }

        if (storeSubscriptionId != null) {
            StoreSubscription storeSubscription = storeSubscriptionService.convertToEntity(storeSubscriptionService.getStoreSubscriptionById(storeSubscriptionId));
            purchase.setStoreSubscription(storeSubscription);
        }

        if (purchaseDetailDTOs != null && !purchaseDetailDTOs.isEmpty()) {
            List<PurchaseDetail> purchaseDetails = new ArrayList<>();
            for (PurchaseDetailRequestDTO detailDTO : purchaseDetailDTOs) {
                Product product = productRepository.findById(detailDTO.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + detailDTO.getProductId()));

                // Verificar si hay suficiente stock para este producto
                Product updatedProduct = productService.updateStockPurchase(product.getId(), Long.valueOf(detailDTO.getQuantity()));

                // Si hay suficiente stock, crear el detalle de compra
                PurchaseDetail detail = new PurchaseDetail();
                detail.setProduct(updatedProduct);
                detail.setQuantity(detailDTO.getQuantity());
                purchaseDetails.add(detail);
            }
            purchase.setPurchaseDetails(purchaseDetails);
        }

        List<CreateCouponDTO> couponsDTO = requestDTO.getCouponsApplied().stream()
                .map(coupon -> CreateCouponDTO.builder()
                        .issueDate(coupon.getIssueDate())
                        .dueDate(coupon.getDueDate())
                        .amount(coupon.getAmount())
                        .spent(coupon.getSpent())
                        .account(coupon.getAccount())
                        .build())
                .collect(Collectors.toList());
        // Aplicar cupones si están presentes en la solicitud
        if (!couponsDTO.isEmpty()) {
            List<Coupon> coupons = couponsDTO.stream()
                    .map(couponDTO -> Coupon.builder()
                            .issueDate(couponDTO.getIssueDate())
                            .dueDate(couponDTO.getDueDate())
                            .amount(couponDTO.getAmount())
                            .spent(couponDTO.getSpent())
                            .account(couponDTO.getAccount())
                            .build())
                    .collect(Collectors.toList());
            purchase.setCouponsApplied(coupons);
        }

        // Guardar la compra
        purchase = purchaseRepository.save(purchase);

        // Realizar otros cálculos y obtener la respuesta
        List<PurchaseDetailResponseDTO> detailDTOs = new ArrayList<>();
        if (purchase.getPurchaseDetails() != null) {
            detailDTOs = purchase.getPurchaseDetails().stream()
                    .map(detail -> {
                        PurchaseDetailResponseDTO detailDTO = new PurchaseDetailResponseDTO();
                        detailDTO.setProductName(detail.getProduct().getName());
                        detailDTO.setQuantity(detail.getQuantity());
                        detailDTO.setSubtotal(calculateSubtotal(detail));
                        return detailDTO;
                    })
                    .collect(Collectors.toList());
        }

        List<CouponResponseDTO> couponsResponseDTO = requestDTO.getCouponsApplied().stream()
                .map(coupon -> new CouponResponseDTO(coupon.getId(), coupon.getAmount()))
                .collect(Collectors.toList());

        // Calcular totales y descuentos
        Double total = (detailDTOs.stream().mapToDouble(PurchaseDetailResponseDTO::getSubtotal).sum()) + purchase.getStoreSubscription().getPrice();
        total = Math.round(total * 100.0) / 100.0;
        Double discount = couponsResponseDTO.stream().mapToDouble(CouponResponseDTO::getAmount).sum();
        Double totalAfterDiscounts = total - discount;
        totalAfterDiscounts = Math.round(totalAfterDiscounts * 100.0) / 100.0;

        Double subscriptionPrice = purchase.getStoreSubscription() != null ? purchase.getStoreSubscription().getPrice() : 0;

        return new PurchaseResponseDTO(detailDTOs, subscriptionPrice, total, couponsResponseDTO, discount, totalAfterDiscounts);
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