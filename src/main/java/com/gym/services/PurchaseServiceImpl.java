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
import com.gym.exceptions.InsufficientCreditException;
import com.gym.repositories.AccountRepository;
import com.gym.repositories.ProductRepository;
import com.gym.repositories.PurchaseRepository;
import com.gym.services.ale.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
//@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService{

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final AccountService accountService;
    private final StoreSubscriptionService storeSubscriptionService;
    private final CouponService couponService;
    private final CouponGenerationService couponGenerationService;
    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository,
                               ProductRepository productRepository,
                               ProductService productService,
                               AccountService accountService,
                               StoreSubscriptionService storeSubscriptionService,
                               CouponService couponService,
                               @Lazy CouponGenerationService couponGenerationService) {
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.accountService = accountService;
        this.storeSubscriptionService = storeSubscriptionService;
        this.couponService = couponService;
        this.couponGenerationService = couponGenerationService;
    }

    public PurchaseResponseDTO createPurchase(PurchaseRequestDTO requestDTO, String token) {
        // Obtener la cuenta del usuario a partir del token
        Account account = getAccountFromToken(token);

        // Construir la compra con los detalles proporcionados en la solicitud
        Purchase purchase = buildPurchase(requestDTO, account);

        // Agregar los cupones a la compra
        addCouponsToPurchase(requestDTO, purchase);

        // Calcular totales y descuentos, y guardar la compra
        calculateAndSavePurchaseTotals(purchase, token);

        // Construir y devolver la respuesta de la compra
        return buildPurchaseResponse(purchase);
    }

    private Double calculateSubtotal(PurchaseDetail purchaseDetail) {
        if (purchaseDetail.getProduct() != null && purchaseDetail.getProduct().getPrice() != null) {
            return purchaseDetail.getProduct().getPrice().doubleValue() * purchaseDetail.getQuantity();
        } else {
            // Manejo del caso en que el precio del producto es null
            throw new IllegalArgumentException("Price of the product is null");
        }
    }

//    private Double calculateTotalAfterDiscounts(Double total, List<Coupon> couponsApplied, StoreSubscription storeSubscription) {
//        Double totalAfterCoupons = total - couponsApplied.stream().mapToDouble(Coupon::getAmount).sum();
//        return totalAfterCoupons - storeSubscription.getPrice();
//    }

    private Account getAccountFromToken(String token) {
        Account account = accountService.getAccountFromToken(token);
        if (account == null) {
            throw new IllegalArgumentException("No se pudo obtener la cuenta del usuario");
        }
        return account;
    }

    private Purchase buildPurchase(PurchaseRequestDTO requestDTO, Account account) {
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

                Product updatedProduct = productService.updateStockPurchase(product.getId(), Long.valueOf(detailDTO.getQuantity()));

                PurchaseDetail detail = new PurchaseDetail();
                detail.setProduct(updatedProduct);
                detail.setQuantity(detailDTO.getQuantity());
                purchaseDetails.add(detail);
            }
            purchase.setPurchaseDetails(purchaseDetails);
        }
        return purchase;
    }

    private void calculateAndSavePurchaseTotals(Purchase purchase, String token) {

        Double total = calculateTotal(purchase);
        Double discount = calculateDiscount(purchase);
        Double totalAfterDiscounts = total - discount;

        BigDecimal creditBalance = accountService.getAccountCreditBalance(getAccountFromToken(token));
        BigDecimal totalAfterDiscountsBigDecimal = BigDecimal.valueOf(totalAfterDiscounts);

        if (totalAfterDiscountsBigDecimal.compareTo(creditBalance) <= 0) {
            accountService.sustractFromCreditBalance(getAccountFromToken(token), totalAfterDiscountsBigDecimal);

            purchase = purchaseRepository.save(purchase);
            couponGenerationService.createCouponByPurchase(getAccountFromToken(token), BigDecimal.valueOf(total));
        } else {
            throw new InsufficientCreditException("El saldo de crédito de la cuenta es insuficiente para realizar la compra");
        }
    }

    public Double calculateTotal(Purchase purchase) {
        Double total = 0.0;

        if (purchase.getPurchaseDetails() != null) {
            total += purchase.getPurchaseDetails().stream()
                    .mapToDouble(detail -> detail.getProduct().getPrice() * detail.getQuantity())
                    .sum();
        }
        if (purchase.getStoreSubscription() != null) {
            total += purchase.getStoreSubscription().getPrice();
        }
        return Math.round(total * 100.0) / 100.0;
    }

    private Double calculateDiscount(Purchase purchase) {
        Double discount = 0.0;

        if (purchase.getCouponsApplied() != null) {
            discount += purchase.getCouponsApplied().stream()
                    .mapToDouble(Coupon::getAmount)
                    .sum();
        }
        return discount;
    }

    private PurchaseResponseDTO buildPurchaseResponse(Purchase purchase) {
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
        Double subscriptionPrice = purchase.getStoreSubscription() != null ? purchase.getStoreSubscription().getPrice() : 0;
        Double total = calculateTotal(purchase);
        Double discount = calculateDiscount(purchase);
        Double totalAfterDiscounts = total - discount;
        totalAfterDiscounts = Math.round(totalAfterDiscounts * 100.0) / 100.0;

        List<CouponResponseDTO> couponsResponseDTO = new ArrayList<>();
        if (purchase.getCouponsApplied() != null) {
            couponsResponseDTO = purchase.getCouponsApplied().stream()
                    .map(coupon -> new CouponResponseDTO(coupon.getId(), coupon.getAmount()))
                    .collect(Collectors.toList());
        }
        return new PurchaseResponseDTO(detailDTOs, subscriptionPrice, total, couponsResponseDTO, discount, totalAfterDiscounts);
    }

    private void addCouponsToPurchase(PurchaseRequestDTO requestDTO, Purchase purchase) {
        List<Long> couponIds = requestDTO.getCouponsIds();
        if (couponIds != null && !couponIds.isEmpty()) {
            List<Coupon> appliedCoupons = new ArrayList<>();
            for (Long couponId : couponIds) {
                try {
                    ResponseCouponDTO responseCouponDTO = couponService.getCouponById(couponId);
                    if (responseCouponDTO != null) {

                        Coupon coupon = new Coupon();
                        coupon.setId(responseCouponDTO.getId());
                        coupon.setIssueDate(responseCouponDTO.getIssueDate());
                        coupon.setDueDate(responseCouponDTO.getDueDate());
                        coupon.setAmount(responseCouponDTO.getAmount());
                        coupon.setSpent(responseCouponDTO.getSpent());

                        appliedCoupons.add(coupon);
                    } else {
                        logger.error("No se encontró ningún cupón con ID: {}", couponId);
                        throw new IllegalArgumentException("Coupon not found with ID: " + couponId);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error("Error al buscar el cupón con ID: {}", couponId, e);
                    throw new IllegalArgumentException("Error al buscar el cupón con ID: " + couponId);
                }
            }
            purchase.setCouponsApplied(appliedCoupons);
        }
    }
}