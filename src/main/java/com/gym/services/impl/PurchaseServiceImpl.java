package com.gym.services.impl;

import com.gym.dto.CouponResponseDTO;
import com.gym.dto.request.DateRangeDTO;
import com.gym.dto.request.PurchaseDetailRequestDTO;
import com.gym.dto.request.PurchaseRequestDTO;
import com.gym.dto.response.*;
import com.gym.entities.*;
import com.gym.enums.ERank;
import com.gym.exceptions.*;
import com.gym.repositories.ProductRepository;
import com.gym.repositories.PurchaseDetailRepository;
import com.gym.repositories.PurchaseRepository;
import com.gym.repositories.RankRepository;
import com.gym.security.configuration.utils.AccountTokenUtils;
import com.gym.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
//@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final AccountService accountService;
    private final StoreSubscriptionService storeSubscriptionService;
    private final CouponService couponService;
    private final CouponGenerationService couponGenerationService;
    private final SubscriptionService subscriptionService;
    private final AccountTokenUtils accountTokenUtils;
    private final PurchaseDetailRepository purchaseDetailRepository;
    private final RankRepository rankRepository;
    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository,
                               ProductRepository productRepository,
                               ProductService productService,
                               AccountService accountService,
                               StoreSubscriptionService storeSubscriptionService,
                               CouponService couponService,
                               @Lazy CouponGenerationService couponGenerationService,
                               SubscriptionService subscriptionService,
                               AccountTokenUtils accountTokenUtils,
                               PurchaseDetailRepository purchaseDetailRepository,
                               RankRepository rankRepository) {
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.accountService = accountService;
        this.storeSubscriptionService = storeSubscriptionService;
        this.couponService = couponService;
        this.couponGenerationService = couponGenerationService;
        this.subscriptionService = subscriptionService;
        this.accountTokenUtils = accountTokenUtils;
        this.purchaseDetailRepository = purchaseDetailRepository;
        this.rankRepository = rankRepository;
    }

    public PurchaseResponseDTO createPurchase(PurchaseRequestDTO requestDTO, String token) {
//        Account account = getAccountFromToken(token);
        Account account = accountService.getAccountById(requestDTO.getAccountId());
        Purchase purchase = buildPurchase(requestDTO, account);
        addCouponsToPurchase(requestDTO, purchase);
        calculateAndSavePurchaseTotals(purchase, token);
        return buildPurchaseResponse(purchase);
    }

    private Double calculateSubtotal(PurchaseDetail purchaseDetail) {
        if (purchaseDetail.getProduct() != null && purchaseDetail.getProduct().getPrice() != null) {
            return purchaseDetail.getProduct().getPrice().doubleValue() * purchaseDetail.getQuantity();
        } else {
            throw new IllegalArgumentException("Price of the product is null");
        }
    }

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

                purchaseDetailRepository.save(detail);
            }
            purchase.setPurchaseDetails(purchaseDetails);
        }
        return purchase;
    }

    private boolean isSubscriptionExpired(Subscription subscription) {
        LocalDate currentDate = LocalDate.now();
        return subscription.getEndDate().isBefore(currentDate);
    }

    private void calculateAndSavePurchaseTotals(Purchase purchase, String token) {

        Double total = calculateTotal(purchase);
        Double discount = calculateDiscount(purchase);
        Double totalAfterDiscounts = total - discount;

        BigDecimal creditBalance = accountService.getAccountCreditBalance(getAccountFromToken(token));
        BigDecimal totalAfterDiscountsBigDecimal = BigDecimal.valueOf(totalAfterDiscounts);

        if (discount * 2 > total) {
            throw new CouponDiscountExceededException("La suma de los descuentos de los cupones no puede superar el 50% del total de la compra");
        }

        if (totalAfterDiscountsBigDecimal.compareTo(creditBalance) <= 0) {
            accountService.sustractFromCreditBalance(getAccountFromToken(token), totalAfterDiscountsBigDecimal);

            purchase = purchaseRepository.save(purchase);

            if (purchase.getPurchaseDetails() != null) {
                for (PurchaseDetail detail : purchase.getPurchaseDetails()) {
                    detail.setPurchase(purchase);
                }
            }
            purchaseDetailRepository.saveAll(purchase.getPurchaseDetails());

            couponGenerationService.createCouponByPurchase(getAccountFromToken(token), BigDecimal.valueOf(total));
        } else {
            throw new InsufficientCreditException("El saldo de crédito de la cuenta es insuficiente para realizar la compra");
        }

        Account account = purchase.getAccount();
        Subscription personalSubscription = subscriptionService.getSubscriptionByAccountId(account.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se pudo obtener la suscripcion con ID: " + account.getId()));
        if (personalSubscription != null && isSubscriptionExpired(personalSubscription)) {
            StoreSubscription storeSubscription = purchase.getStoreSubscription();
            if (storeSubscription != null) {
                subscriptionService.updateSubscriptionPurchase(storeSubscription, token);
                accountService.updateSubscription(account);
            } else {
                logger.warn("User's personal subscription is expired, but no store subscription found for purchase with ID {}", purchase.getId());
            }
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

    @Override
    public PurchaseResponseDTO buildPurchaseResponse(Purchase purchase) {
        List<PurchaseDetailResponseDTO> detailDTOs = new ArrayList<>();
        if (purchase.getPurchaseDetails() != null) {
            detailDTOs = purchase.getPurchaseDetails().stream()
                    .map(detail -> {
                        PurchaseDetailResponseDTO detailDTO = new PurchaseDetailResponseDTO();
                        detailDTO.setProductName(detail.getProduct().getName());
                        detailDTO.setQuantity(detail.getQuantity());
                        detailDTO.setSubtotal(Math.round(calculateSubtotal(detail)*100d)/100d);
                        return detailDTO;
                    })
                    .collect(Collectors.toList());
        }
        Double subscriptionPrice = purchase.getStoreSubscription() != null ? purchase.getStoreSubscription().getPrice() : 0;
        Double total = Math.round(calculateTotal(purchase)*100d)/100d;
        Double discount = Math.round(calculateDiscount(purchase)*100d)/100d;
        Double totalAfterDiscounts = total - discount;
        totalAfterDiscounts = Math.round(totalAfterDiscounts * 100.0d) / 100.0d;

        List<com.gym.dto.response.CouponResponseDTO> couponsResponseDTO = new ArrayList<>();
        if (purchase.getCouponsApplied() != null) {
            couponsResponseDTO = purchase.getCouponsApplied().stream()
                    .map(coupon -> new com.gym.dto.response.CouponResponseDTO(coupon.getId(), coupon.getAmount()))
                    .collect(Collectors.toList());
        }
        Long purchaseId = purchase.getId();
        LocalDate purchaseDate = purchase.getPurchaseDate();
        return new PurchaseResponseDTO(purchaseId, purchaseDate, detailDTOs, subscriptionPrice, total, couponsResponseDTO, discount, totalAfterDiscounts);
    }

    private void addCouponsToPurchase(PurchaseRequestDTO requestDTO, Purchase purchase) {
        List<Long> couponIds = requestDTO.getCouponsIds();
        if (couponIds != null && !couponIds.isEmpty()) {
            List<Coupon> appliedCoupons = new ArrayList<>();
            for (Long couponId : couponIds) {
                try {
                    CouponResponseDTO couponResponseDTO = couponService.getCouponById(couponId);
                    if (couponResponseDTO != null) {
                        if (couponResponseDTO.getSpent()) {
                            throw new IllegalStateException("El cupón con ID " + couponId + " ya ha sido gastado.");
                        }
                        Coupon coupon = new Coupon();
                        coupon.setId(couponResponseDTO.getId());
                        coupon.setIssueDate(couponResponseDTO.getIssueDate());
                        coupon.setDueDate(couponResponseDTO.getDueDate());
                        coupon.setAmount(couponResponseDTO.getAmount());
                        coupon.setSpent(couponResponseDTO.getSpent());

                        appliedCoupons.add(coupon);
                        couponService.markCouponAsSpent(couponId);
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

    public Map<String, Double> calculateSalesByCategory(LocalDate startDate, LocalDate endDate) {
        List<Purchase> purchases = purchaseRepository.findAllByPurchaseDateBetween(startDate, endDate);
        Map<String, Double> salesByCategory = new HashMap<>();

        for (Purchase purchase : purchases) {
            List<PurchaseDetail> purchaseDetails = purchase.getPurchaseDetails();
            for (PurchaseDetail detail : purchaseDetails) {
                Product product = detail.getProduct();
                String category = product.getCategory().getName(); // Obtiene el nombre de la categoría del producto
                Double subtotal = calculateSubtotal(detail);
                salesByCategory.put(category, salesByCategory.getOrDefault(category, 0.0) + subtotal);
            }
        }

        return salesByCategory;
    }

    public List<PurchaseResponseDTO> getPurchasesByAccount(Long accountId, HttpServletRequest request){
        try {
            boolean hasAccess = accountTokenUtils.hasAccessToAccount(request, accountId);
            if (!hasAccess) {
                throw new UnauthorizedException("Access denied to purchases for account with ID " + accountId);
            }
            List<Purchase> accountPurchases = purchaseRepository.findByAccountId(accountId);
            if (accountPurchases.isEmpty()) {
                throw new ResourceNotFoundException("No purchases found for account with ID " + accountId);
            }

            List<PurchaseResponseDTO> purchaseResponseDTOs = new ArrayList<>();
            for (Purchase purchase : accountPurchases) {
                List<PurchaseDetailResponseDTO> purchaseDetailResponseDTOs = new ArrayList<>();
                double total = 0.0;
                double discount = 0.0;

                for (PurchaseDetail purchaseDetail : purchase.getPurchaseDetails()) {
                    double subtotal = Math.round(purchaseDetail.getProduct().getPrice() * purchaseDetail.getQuantity()*100d)/100d;
                    purchaseDetailResponseDTOs.add(new PurchaseDetailResponseDTO(
                            purchaseDetail.getProduct().getName(),
                            purchaseDetail.getQuantity(),
                            subtotal
                    ));
                    total += subtotal;
                }

                StoreSubscription storeSubscription = purchase.getStoreSubscription();
                if (storeSubscription != null) {
                    total += storeSubscription.getPrice();
                }

                List<com.gym.dto.response.CouponResponseDTO> couponResponseDTOs = new ArrayList<>();
                for (Coupon coupon : purchase.getCouponsApplied()) {
                    couponResponseDTOs.add( new com.gym.dto.response.CouponResponseDTO(
                            coupon.getId(),
                            coupon.getAmount()
                    ));
                    discount += coupon.getAmount();
                }

                double totalAfterDiscounts = total - discount;

                PurchaseResponseDTO purchaseResponseDTO = new PurchaseResponseDTO(
                        purchase.getId(),
                        purchase.getPurchaseDate(),
                        purchaseDetailResponseDTOs,
                        storeSubscription != null ? storeSubscription.getPrice() : null,
                        Math.round(total*100)/100d,
                        couponResponseDTOs,
                        Math.round(discount*100)/100d,
                        Math.round(totalAfterDiscounts*100)/100d
                );
                purchaseResponseDTOs.add(purchaseResponseDTO);
            }

            return purchaseResponseDTOs;
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error occurred", e);
        }
    }

    @Override
    public List<PurchaseResponseDTO> getAllPurchases(HttpServletRequest request) {
        try {
            boolean isAdmin = accountTokenUtils.isAdminFromToken(request);

            List<Purchase> allPurchases;
            if (isAdmin) {
                allPurchases = purchaseRepository.findAll();
            } else {
                throw new UnauthorizedException("No tienes permiso para acceder a todas las compras.");
            }
            return allPurchases.stream()
                    .map(this::buildPurchaseResponse)
                    .collect(Collectors.toList());
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @Override
    public PurchaseResponseDTO getPurchaseById(Long id, HttpServletRequest request) {
        try {
            boolean hasAccess = accountTokenUtils.hasAccessToAccount(request, id);
            if (!hasAccess) {
                throw new UnauthorizedException("Access denied to purchase with ID " + id);
            }
            Purchase purchase = purchaseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Purchase with ID: " + id + " not found"));
            PurchaseResponseDTO purchaseResponseDTO = buildPurchaseResponse(purchase);

            return purchaseResponseDTO;
        } catch (UnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
        }
    }

    @Override
    public List<PurchaseResponseDTO> getAllPurchasesByDateRange(DateRangeDTO dateRangeDTO) {
        try {
            LocalDate startDate = dateRangeDTO.getStartDate();
            LocalDate endDate = dateRangeDTO.getEndDate();
            List<Purchase> purchasesInRange = purchaseRepository.findAllByPurchaseDateBetween(startDate, endDate);
            List<PurchaseResponseDTO> purchaseResponseDTOs = purchasesInRange.stream()
                    .map(this::buildPurchaseResponse)
                    .collect(Collectors.toList());
            return purchaseResponseDTOs;
        } catch (Exception e) {
            throw new ServiceException("Error occurred while retrieving purchases by date range", e);
        }
    }

    @Override
    public Double getTotalAfterDiscountsSumByDateRange(DateRangeDTO dateRangeDTO) {
        List<PurchaseResponseDTO> purchases = getAllPurchasesByDateRange(dateRangeDTO);
        return purchases.stream()
                .mapToDouble(PurchaseResponseDTO::getTotalAfterDiscounts)
                .sum();
    }

    @Override
    public Long getPurchasesCountByDateRange(DateRangeDTO dateRangeDTO) {
        List<PurchaseResponseDTO> purchases = getAllPurchasesByDateRange(dateRangeDTO);
        return purchases.stream()
                .mapToDouble(PurchaseResponseDTO::getTotalAfterDiscounts)
                .count();
    }

    @Override
    public Double getPurchasesAverageByDateRange(DateRangeDTO dateRangeDTO) {
        List<PurchaseResponseDTO> purchases = getAllPurchasesByDateRange(dateRangeDTO);
        return purchases.stream()
                .mapToDouble(PurchaseResponseDTO::getTotalAfterDiscounts)
                .average().orElseThrow(ArithmeticException::new);
    }

    @Override
    public Double calculateAveragePurchaseAmountPerUser() {
        try {
            List<AccountPurchaseDTO> accountsWithPurchases = accountService.getAllAccountsWithPurchasesDTO();
            double totalSpent = accountsWithPurchases.stream()
                    .mapToDouble(account -> account.getPurchases().stream()
                            .mapToDouble(PurchaseResponseDTO::getTotalAfterDiscounts)
                            .sum())
                    .sum();
            int totalUserAccounts = accountsWithPurchases.size();
            if (totalUserAccounts > 0) {
                return totalSpent / totalUserAccounts;
            } else {
                throw new NoAccountsException("There are no registered user accounts.");
            }
        } catch (Exception e) {
            throw new PurchaseServiceException("Error calculating the average spending per user account.", e);
        }
    }

    @Override
    public List<ProductSalesResponseDTO> getUnitsSoldByProduct() {
        Map<Product, Integer> totalUnitsSoldByProduct = calculateTotalUnitsSoldByProduct();
        if (totalUnitsSoldByProduct.isEmpty()) {
            throw new NoDataFoundException("No units sold data available.");
        }
        List<ProductSalesResponseDTO> productSalesResponseDTOList = new ArrayList<>();

        totalUnitsSoldByProduct.forEach((product, unitsSold) -> {
            ProductSalesResponseDTO dto = new ProductSalesResponseDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setUnitsSold(unitsSold);
            dto.setPrice(product.getPrice());
            dto.setCategoryId(product.getCategory().getId());
            productSalesResponseDTOList.add(dto);
        });

        productSalesResponseDTOList.sort(Comparator.comparing(ProductSalesResponseDTO::getUnitsSold).reversed());
        return productSalesResponseDTOList;
    }

    @Override
    public List<ProductAmountResponseDTO> getSalesByProduct() {
        Map<Product, Double> totalSalesByProduct = calculateTotalSalesByProduct();
        if (totalSalesByProduct.isEmpty()) {
            throw new NoDataFoundException("No sales data available.");
        }
        List<ProductAmountResponseDTO> productAmountResponseDTOList = new ArrayList<>();

        totalSalesByProduct.forEach((product, totalSales) -> {
            ProductAmountResponseDTO dto = new ProductAmountResponseDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setTotalSales(totalSales);
            dto.setPrice(product.getPrice());
            dto.setCategoryId(product.getCategory().getId());
            productAmountResponseDTOList.add(dto);
        });

        productAmountResponseDTOList.sort(Comparator.comparing(ProductAmountResponseDTO::getTotalSales).reversed());
        return productAmountResponseDTOList;
    }

    private Map<Product, Integer> calculateTotalUnitsSoldByProduct() {
        Map<Product, Integer> totalUnitsSoldByProduct = new HashMap<>();
        List<Purchase> purchases = purchaseRepository.findAll();

        for (Purchase purchase : purchases) {
            List<PurchaseDetail> purchaseDetails = purchase.getPurchaseDetails();
            for (PurchaseDetail purchaseDetail : purchaseDetails) {
                Product product = purchaseDetail.getProduct();
                Integer quantitySold = purchaseDetail.getQuantity();
                totalUnitsSoldByProduct.put(product, totalUnitsSoldByProduct.getOrDefault(product, 0) + quantitySold);
            }
        }
        return totalUnitsSoldByProduct;
    }

    private Map<Product, Double> calculateTotalSalesByProduct() {
        Map<Product, Double> totalSalesByProduct = new HashMap<>();
        List<Purchase> purchases = purchaseRepository.findAll();

        for (Purchase purchase : purchases) {
            List<PurchaseDetail> purchaseDetails = purchase.getPurchaseDetails();
            for (PurchaseDetail purchaseDetail : purchaseDetails) {
                Product product = purchaseDetail.getProduct();
                Integer quantitySold = purchaseDetail.getQuantity();
                Double subtotal = product.getPrice() * quantitySold;
                totalSalesByProduct.put(product, totalSalesByProduct.getOrDefault(product, 0.0) + subtotal);
            }
        }
        return totalSalesByProduct;
    }
}