package com.gym.services;

import com.gym.dto.CreateCouponDTO;
import com.gym.dto.CreateStoreSubscriptionDTO;
import com.gym.dto.ResponseCouponDTO;
import com.gym.dto.ResponseStoreSubscription;
import com.gym.dto.request.PurchaseDetailRequestDTO;
import com.gym.dto.request.PurchaseRequestDTO;
import com.gym.dto.response.CouponResponseDTO;
import com.gym.dto.response.PurchaseDetailResponseDTO;
import com.gym.dto.response.PurchaseResponseDTO;
import com.gym.entities.*;
import com.gym.repositories.AccountRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService{

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final StoreSubscriptionService storeSubscriptionService;
    private final CouponService couponService;
    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);


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

        // Obtener los IDs de los cupones aplicados y agregarlos a la compra
        List<Long> couponIds = requestDTO.getCouponsIds();
        if (couponIds != null && !couponIds.isEmpty()) {
            List<Coupon> appliedCoupons = new ArrayList<>();
            for (Long couponId : couponIds) {
                // Buscar y convertir el cupón dentro del mismo bloque try-catch
                try {
                    // Buscar el cupón por su ID
                    ResponseCouponDTO responseCouponDTO = couponService.getCouponById(couponId);

                    // Verificar si el cupón fue encontrado
                    if (responseCouponDTO != null) {
                        // Convertir el DTO del cupón a la entidad del cupón
                        Coupon coupon = new Coupon();
                        coupon.setId(responseCouponDTO.getId());
                        coupon.setIssueDate(responseCouponDTO.getIssueDate());
                        coupon.setDueDate(responseCouponDTO.getDueDate());
                        coupon.setAmount(responseCouponDTO.getAmount());
                        coupon.setSpent(responseCouponDTO.getSpent());

                        // Agregar el cupón convertido a la lista de cupones aplicados
                        appliedCoupons.add(coupon);
                    } else {
                        // Si el cupón no fue encontrado, lanzar una excepción
                        logger.error("No se encontró ningún cupón con ID: {}", couponId);
                        throw new IllegalArgumentException("Coupon not found with ID: " + couponId);
                    }
                } catch (IllegalArgumentException e) {
                    // Capturar y relanzar la excepción si ocurre un error al buscar el cupón
                    logger.error("Error al buscar el cupón con ID: {}", couponId, e);
                    throw new IllegalArgumentException("Error al buscar el cupón con ID: " + couponId);
                }
            }
            // Establecer la lista de cupones aplicados en la compra
            purchase.setCouponsApplied(appliedCoupons);
        }

        // Guardar la compra luego de aplicar los cupones
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

        // Calcular totales y descuentos
        Double total = (detailDTOs.stream().mapToDouble(PurchaseDetailResponseDTO::getSubtotal).sum()) + purchase.getStoreSubscription().getPrice();
        total = Math.round(total * 100.0) / 100.0;

        Double discount = 0.0;
        if (purchase.getCouponsApplied() != null) {
            discount = purchase.getCouponsApplied().stream()
                    .mapToDouble(Coupon::getAmount)
                    .sum();
        }

        Double totalAfterDiscounts = total - discount;
        totalAfterDiscounts = Math.round(totalAfterDiscounts * 100.0) / 100.0;

        Double subscriptionPrice = purchase.getStoreSubscription() != null ? purchase.getStoreSubscription().getPrice() : 0;

        // Crear la lista de cupones aplicados para la respuesta
        List<CouponResponseDTO> couponsResponseDTO = new ArrayList<>();
        if (purchase.getCouponsApplied() != null) {
            couponsResponseDTO = purchase.getCouponsApplied().stream()
                    .map(coupon -> new CouponResponseDTO(coupon.getId(), coupon.getAmount()))
                    .collect(Collectors.toList());
        }

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