package com.gym.dto;

import com.gym.entities.Coupon;
import com.gym.entities.Purchase;
import com.gym.entities.Rank;
import com.gym.entities.Transfer;
import com.gym.security.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountDTO {
//    private String username;

    private UserEntity user;
    private List<Transfer> transfers;
    private List<Coupon> coupons;
    private List<Purchase> purchases;
    private BigDecimal creditBalance;
    private Rank rank;

}
