package com.gym.repositories;

import com.gym.entities.Account;
import com.gym.entities.Coupon;
import org.springframework.data.repository.CrudRepository;

public interface CouponRepository extends CrudRepository<Coupon, Long> {
}
