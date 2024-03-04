package com.gym.services;

import com.gym.dto.*;
import com.gym.entities.Coupon;
import com.gym.entities.Purchase;
import com.gym.entities.Subscription;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CouponService {

    List<ResponseCouponDTO> getAllCoupons();
    ResponseCouponDTO getCouponById(Long id);
    ResponseCouponDTO createCoupon(CreateCouponDTO createCouponDTO);
//    Coupon createCouponByPurchase(Purchase purchase);
    ResponseCouponDTO updateCoupon(UpdateCouponDTO updateCouponDTO);
    void deleteCouponById(Long id);
    List<ResponseCouponDTO> getBySpentTrue();
    List<ResponseCouponDTO> getBySpentFalse();
    List<ResponseCouponDTO> getExpiredCoupons();
    List<ResponseCouponDTO> getCurrentCoupons();
    boolean isCouponSpent(Long couponId);
    boolean isCouponExpired(Long couponId);
    public Coupon convertResponseToEntity(ResponseCouponDTO responseCouponDTO);

}
