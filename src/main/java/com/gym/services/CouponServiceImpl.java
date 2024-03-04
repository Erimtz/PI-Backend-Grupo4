package com.gym.services;

import com.gym.dto.CreateCouponDTO;
import com.gym.dto.ResponseCouponDTO;
import com.gym.dto.UpdateCouponDTO;
import com.gym.entities.Account;
import com.gym.entities.Coupon;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.AccountRepository;
import com.gym.repositories.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService{

    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);
    private final CouponRepository couponRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<ResponseCouponDTO> getAllCoupons() {
        return couponRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseCouponDTO getCouponById(Long id) {
        return couponRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> {
                    logger.error("No se encontró ningún cupón con ID: {}", id);
                    return new ResourceNotFoundException("Coupon with ID " + id + " not found");
                });
    }

    @Override
    public ResponseCouponDTO createCoupon(CreateCouponDTO createCouponDTO) {
        try {
            Coupon coupon = Coupon.builder()
                    .issueDate(createCouponDTO.getIssueDate())
                    .dueDate(createCouponDTO.getDueDate())
                    .amount(createCouponDTO.getAmount())
                    .spent(createCouponDTO.getSpent())
                    .account(createCouponDTO.getAccount())
                    .build();
            couponRepository.save(coupon);
            return convertToDto(coupon);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while saving coupon", e);
        }
    }

//    public Coupon createCouponByPurchase(Purchase purchase){
//        try {
//            Account account = purchase.getAccount();
//            if (account == null) {
//                throw new IllegalArgumentException("The purchase is not associated with any account.");
//            }
//
//            ERank rank = account.getRank().getName();
//            Coupon coupon;
//
//            if (rank == ERank.BRONZE) {
//                coupon = createBronzeCoupon(purchase);
//            } else if (rank == ERank.SILVER) {
//                coupon = createSilverCoupon(purchase);
//            } else if (rank == ERank.GOLD) {
//                coupon = createGoldCoupon(purchase);
//            } else if (rank == ERank.PLATINUM) {
//                coupon = createPlatinumCoupon(purchase);
//            } else {
//                throw new IllegalArgumentException("Invalid account rank: " + rank);
//            }
//
//            return coupon;
//        } catch (Exception e) {
//            throw new DatabaseOperationException("Error occurred while creating coupon", e);
//        }
//    }

    @Override
    public ResponseCouponDTO updateCoupon(UpdateCouponDTO updateCouponDTO) {
        Optional<Coupon> couponOptional = couponRepository.findById(updateCouponDTO.getId());
        if (couponOptional.isPresent()){
            Coupon coupon = couponOptional.get();

            if (updateCouponDTO.getIssueDate() != null) {
                coupon.setIssueDate(updateCouponDTO.getIssueDate());
            }
            if (updateCouponDTO.getDueDate() != null) {
                coupon.setDueDate(updateCouponDTO.getDueDate());
            }
            if (updateCouponDTO.getAmount() != null) {
                coupon.setAmount(updateCouponDTO.getAmount());
            }
            if (updateCouponDTO.getSpent() != null) {
                coupon.setSpent(updateCouponDTO.getSpent());
            }
            if (updateCouponDTO.getAccount() != null) {
                coupon.setAccount(updateCouponDTO.getAccount());
            }
            couponRepository.save(coupon);
            return convertToDto(coupon);
        } else {
            throw new ResourceNotFoundException("Coupon with ID " + updateCouponDTO.getId() + " not found");
        }
    }

    @Override
    public void deleteCouponById(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon with ID " + id + " not found");
        }
        couponRepository.deleteById(id);
    }

    @Override
    public List<ResponseCouponDTO> getBySpentTrue() {
        return couponRepository.findBySpentTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseCouponDTO> getBySpentFalse() {
        return couponRepository.findBySpentFalse()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseCouponDTO> getExpiredCoupons() {
        return couponRepository.findExpiredCoupons()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseCouponDTO> getCurrentCoupons() {
        return couponRepository.findCurrentCoupons()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markCouponAsSpent(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon with ID " + couponId + " not found"));
        coupon.setSpent(true);
        couponRepository.save(coupon);
    }

    @Override
    public boolean isCouponSpent(Long couponId) {
        return couponRepository.isCouponSpent(couponId);
    }

    @Override
    public boolean isCouponExpired(Long couponId) {
        return couponRepository.isCouponExpired(couponId);
    }

//    private Coupon createBronzeCoupon(Purchase purchase) {
//        try {
//            LocalDate currentDate = LocalDate.now();
//
//            BigDecimal totalPurchaseAmount = BigDecimal.valueOf(purchaseService.calculateTotal(purchase));
////            BigDecimal totalPurchaseAmount = purchase.calculateTotalAmount(); // Hay que crear el metodo en purchase, esta en purchase y da 1 pero tiene que ir en el futuro service
//
//            LocalDate dueDate;
//            if (totalPurchaseAmount.compareTo(new BigDecimal("50")) < 0) {
//                dueDate = currentDate.plusDays(7);
//            } else if (totalPurchaseAmount.compareTo(new BigDecimal("500")) >= 0) {
//                dueDate = currentDate.plusDays(28);
//            } else {
//                dueDate = currentDate.plusDays(14);
//            }
//
//            BigDecimal couponAmount = totalPurchaseAmount.multiply(new BigDecimal("0.05"));
//
//            Coupon coupon = Coupon.builder()
//                    .issueDate(currentDate)
//                    .dueDate(dueDate)
//                    .amount(couponAmount.doubleValue())
//                    .spent(false)
//                    .account(purchase.getAccount())
//                    .build();
//
//            return couponRepository.save(coupon);
//        } catch (Exception e) {
//            throw new DatabaseOperationException("Error occurred while creating bronze coupon", e);
//        }
//    }
//
//    private Coupon createSilverCoupon(Purchase purchase) {
//        try {
//            LocalDate currentDate = LocalDate.now();
//
//            BigDecimal totalPurchaseAmount = BigDecimal.valueOf(purchaseService.calculateTotal(purchase));
////            BigDecimal totalPurchaseAmount = purchase.calculateTotalAmount(); // Hay que crear el metodo en purchase, esta en purchase y da 1 pero tiene que ir en el futuro service
//
//            LocalDate dueDate;
//            if (totalPurchaseAmount.compareTo(new BigDecimal("50")) < 0) {
//                dueDate = currentDate.plusDays(7);
//            } else if (totalPurchaseAmount.compareTo(new BigDecimal("500")) >= 0) {
//                dueDate = currentDate.plusDays(28);
//            } else {
//                dueDate = currentDate.plusDays(14);
//            }
//
//            BigDecimal couponAmount = totalPurchaseAmount.multiply(new BigDecimal("0.07"));
//
//            Coupon coupon = Coupon.builder()
//                    .issueDate(currentDate)
//                    .dueDate(dueDate)
//                    .amount(couponAmount.doubleValue())
//                    .spent(false)
//                    .account(purchase.getAccount())
//                    .build();
//
//            return couponRepository.save(coupon);
//        } catch (Exception e) {
//            throw new DatabaseOperationException("Error occurred while creating silver coupon", e);
//        }
//    }
//
//    private Coupon createGoldCoupon(Purchase purchase) {
//        try {
//            LocalDate currentDate = LocalDate.now();
//
//            BigDecimal totalPurchaseAmount = BigDecimal.valueOf(purchaseService.calculateTotal(purchase));
////            BigDecimal totalPurchaseAmount = purchase.calculateTotalAmount(); // Hay que crear el metodo en purchase, esta en purchase y da 1 pero tiene que ir en el futuro service
//
//            LocalDate dueDate;
//            if (totalPurchaseAmount.compareTo(new BigDecimal("50")) < 0) {
//                dueDate = currentDate.plusDays(14);
//            } else if (totalPurchaseAmount.compareTo(new BigDecimal("500")) >= 0) {
//                dueDate = currentDate.plusDays(63);
//            } else {
//                dueDate = currentDate.plusDays(28);
//            }
//
//            BigDecimal couponAmount = totalPurchaseAmount.multiply(new BigDecimal("0.08"));
//
//            Coupon coupon = Coupon.builder()
//                    .issueDate(currentDate)
//                    .dueDate(dueDate)
//                    .amount(couponAmount.doubleValue())
//                    .spent(false)
//                    .account(purchase.getAccount())
//                    .build();
//
//            return couponRepository.save(coupon);
//        } catch (Exception e) {
//            throw new DatabaseOperationException("Error occurred while creating gold coupon", e);
//        }
//    }
//
//    private Coupon createPlatinumCoupon(Purchase purchase) {
//        try {
//            LocalDate currentDate = LocalDate.now();
//
//            BigDecimal totalPurchaseAmount = BigDecimal.valueOf(purchaseService.calculateTotal(purchase));
////            BigDecimal totalPurchaseAmount = purchase.calculateTotalAmount(); // Hay que crear el metodo en purchase, esta en purchase y da 1 pero tiene que ir en el futuro service
//
//            LocalDate dueDate;
//            if (totalPurchaseAmount.compareTo(new BigDecimal("50")) < 0) {
//                dueDate = currentDate.plusDays(14);
//            } else if (totalPurchaseAmount.compareTo(new BigDecimal("500")) >= 0) {
//                dueDate = currentDate.plusDays(63);
//            } else {
//                dueDate = currentDate.plusDays(28);
//            }
//
//            BigDecimal couponAmount = totalPurchaseAmount.multiply(new BigDecimal("0.10"));
//
//            Coupon coupon = Coupon.builder()
//                    .issueDate(currentDate)
//                    .dueDate(dueDate)
//                    .amount(couponAmount.doubleValue())
//                    .spent(false)
//                    .account(purchase.getAccount())
//                    .build();
//
//            return couponRepository.save(coupon);
//        } catch (Exception e) {
//            throw new DatabaseOperationException("Error occurred while creating platinum coupon", e);
//        }
//    }

    private ResponseCouponDTO convertToDto(Coupon coupon) {
        return new ResponseCouponDTO(
                coupon.getId(),
                coupon.getIssueDate(),
                coupon.getDueDate(),
                coupon.getAmount(),
                coupon.getSpent(),
                coupon.getAccount().getId()
        );
    }

    public Coupon convertResponseToEntity(ResponseCouponDTO responseCouponDTO) {
        Account account = accountRepository.findByUserId(responseCouponDTO.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta asociada al cupón"));

        Coupon coupon = new Coupon();
        coupon.setId(responseCouponDTO.getId());
        coupon.setIssueDate(responseCouponDTO.getIssueDate());
        coupon.setDueDate(responseCouponDTO.getDueDate());
        coupon.setAmount(responseCouponDTO.getAmount());
        coupon.setSpent(responseCouponDTO.getSpent());
        coupon.setAccount(account);

        return coupon;
    }

}
