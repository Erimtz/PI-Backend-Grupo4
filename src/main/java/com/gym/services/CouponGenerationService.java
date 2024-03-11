package com.gym.services;

import com.gym.entities.Account;
import com.gym.entities.Coupon;

import java.math.BigDecimal;

public interface CouponGenerationService {
    Coupon createCouponByPurchase(Account account, BigDecimal totalAmountPurchase);
}
