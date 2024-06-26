package com.gym.repositories;

import com.gym.dto.request.DateRangeDTO;
import com.gym.entities.Coupon;
import com.gym.entities.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    //   @Query(value = "select * from purchase where product_id like :productId",nativeQuery = true)
    //    List<Purchase> findByProduct_Id(@Param("productId") Integer productId);
    //
    //    @Query(value = "select * from purchase where user_id like :userId",nativeQuery = true)
    //    List<Purchase> findByUser_Id(@Param("userId") Integer userId);
    List<Purchase> findByAccountId(Long accountId);
    List<Purchase> findAllByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);

}
