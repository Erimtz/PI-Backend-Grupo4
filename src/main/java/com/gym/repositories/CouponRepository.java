package com.gym.repositories;


import com.gym.entities.Coupon;
import com.gym.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findBySpentTrue();
    List<Coupon> findBySpentFalse();
    @Query("SELECT s FROM Coupon s WHERE s.dueDate < CURRENT_DATE")
    List<Coupon> findExpiredCoupons();
    @Query("SELECT s FROM Coupon s WHERE s.dueDate >= CURRENT_DATE")
    List<Coupon> findCurrentCoupons();
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Coupon s WHERE s.id = :couponId AND s.spent = true")
    boolean isCouponSpent(Long couponId);
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Coupon s WHERE s.id = :couponId AND s.dueDate < CURRENT_DATE()")
    boolean isCouponExpired(Long couponId);
}
