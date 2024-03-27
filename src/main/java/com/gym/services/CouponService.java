package com.gym.services;

import com.gym.dto.*;
import com.gym.dto.request.CouponCreateDTO;
import com.gym.dto.request.CouponUpdateDTO;
import com.gym.entities.Coupon;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CouponService {

    List<CouponResponseDTO> getAllCoupons();
    CouponResponseDTO getCouponById(Long id);
    CouponResponseDTO createCoupon(CouponCreateDTO couponCreateDTO);
    CouponResponseDTO updateCoupon(CouponUpdateDTO couponUpdateDTO);
    void deleteCouponById(Long id);
    List<CouponResponseDTO> getBySpentTrue();
    List<CouponResponseDTO> getBySpentFalse();
    List<CouponResponseDTO> getExpiredCoupons();
    List<CouponResponseDTO> getCurrentCoupons();
    void markCouponAsSpent(Long couponId);
    boolean isCouponSpent(Long couponId);
    boolean isCouponExpired(Long couponId);
    public Coupon convertResponseToEntity(CouponResponseDTO couponResponseDTO);
    List<CouponResponseDTO> getValidCouponsByAccount(Long accountId, HttpServletRequest request);
    List<CouponResponseDTO> getCouponsByAccount(Long accountId, HttpServletRequest request);

    double calculateCouponEffectiveness();
}
