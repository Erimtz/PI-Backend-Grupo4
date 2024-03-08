package com.gym.services.impl;

import com.gym.entities.Account;
import com.gym.entities.Coupon;
import com.gym.enums.ERank;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.repositories.CouponRepository;
import com.gym.services.CouponGenerationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class CouponGenerationServiceImpl implements CouponGenerationService {

    private final CouponRepository couponRepository;

    public CouponGenerationServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public Coupon createCouponByPurchase(Account account, BigDecimal totalPurchaseAmount){
        try {
            if (account == null) {
                throw new IllegalArgumentException("The purchase is not associated with any account.");
            }
            ERank rank = account.getRank().getName();
            Coupon coupon;

            if (rank == ERank.BRONZE) {
                coupon = createBronzeCoupon(totalPurchaseAmount, account);
            } else if (rank == ERank.SILVER) {
                coupon = createSilverCoupon(totalPurchaseAmount, account);
            } else if (rank == ERank.GOLD) {
                coupon = createGoldCoupon(totalPurchaseAmount, account);
            } else if (rank == ERank.PLATINUM) {
                coupon = createPlatinumCoupon(totalPurchaseAmount, account);
            } else {
                throw new IllegalArgumentException("Invalid account rank: " + rank);
            }
            return coupon;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while creating coupon", e);
        }
    }

    private Coupon createBronzeCoupon(BigDecimal totalPurchaseAmount, Account account) {
        try {
            LocalDate currentDate = LocalDate.now();

            LocalDate dueDate;
            if (totalPurchaseAmount.compareTo(new BigDecimal("50")) < 0) {
                dueDate = currentDate.plusDays(7);
            } else if (totalPurchaseAmount.compareTo(new BigDecimal("500")) >= 0) {
                dueDate = currentDate.plusDays(28);
            } else {
                dueDate = currentDate.plusDays(14);
            }

            BigDecimal couponAmount = totalPurchaseAmount.multiply(new BigDecimal("0.05"));

            Coupon coupon = Coupon.builder()
                    .issueDate(currentDate)
                    .dueDate(dueDate)
                    .amount(couponAmount.setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .spent(false)
                    .account(account)
                    .build();

            return couponRepository.save(coupon);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while creating bronze coupon", e);
        }
    }

    private Coupon createSilverCoupon(BigDecimal totalPurchaseAmount, Account account) {
        try {
            LocalDate currentDate = LocalDate.now();

            LocalDate dueDate;
            if (totalPurchaseAmount.compareTo(new BigDecimal("50")) < 0) {
                dueDate = currentDate.plusDays(7);
            } else if (totalPurchaseAmount.compareTo(new BigDecimal("500")) >= 0) {
                dueDate = currentDate.plusDays(28);
            } else {
                dueDate = currentDate.plusDays(14);
            }

            BigDecimal couponAmount = totalPurchaseAmount.multiply(new BigDecimal("0.07"));

            Coupon coupon = Coupon.builder()
                    .issueDate(currentDate)
                    .dueDate(dueDate)
                    .amount(couponAmount.setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .spent(false)
                    .account(account)
                    .build();

            return couponRepository.save(coupon);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while creating silver coupon", e);
        }
    }

    private Coupon createGoldCoupon(BigDecimal totalPurchaseAmount, Account account) {
        try {
            LocalDate currentDate = LocalDate.now();

            LocalDate dueDate;
            if (totalPurchaseAmount.compareTo(new BigDecimal("50")) < 0) {
                dueDate = currentDate.plusDays(14);
            } else if (totalPurchaseAmount.compareTo(new BigDecimal("500")) >= 0) {
                dueDate = currentDate.plusDays(63);
            } else {
                dueDate = currentDate.plusDays(28);
            }

            BigDecimal couponAmount = totalPurchaseAmount.multiply(new BigDecimal("0.08"));

            Coupon coupon = Coupon.builder()
                    .issueDate(currentDate)
                    .dueDate(dueDate)
                    .amount(couponAmount.setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .spent(false)
                    .account(account)
                    .build();

            return couponRepository.save(coupon);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while creating gold coupon", e);
        }
    }

    private Coupon createPlatinumCoupon(BigDecimal totalPurchaseAmount, Account account) {
        try {
            LocalDate currentDate = LocalDate.now();

            LocalDate dueDate;
            if (totalPurchaseAmount.compareTo(new BigDecimal("50")) < 0) {
                dueDate = currentDate.plusDays(14);
            } else if (totalPurchaseAmount.compareTo(new BigDecimal("500")) >= 0) {
                dueDate = currentDate.plusDays(63);
            } else {
                dueDate = currentDate.plusDays(28);
            }

            BigDecimal couponAmount = totalPurchaseAmount.multiply(new BigDecimal("0.10"));

            Coupon coupon = Coupon.builder()
                    .issueDate(currentDate)
                    .dueDate(dueDate)
                    .amount(couponAmount.setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .spent(false)
                    .account(account)
                    .build();

            return couponRepository.save(coupon);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while creating platinum coupon", e);
        }
    }
}
