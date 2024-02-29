package com.gym.dto;

import com.gym.entities.Account;
import com.gym.entities.Coupon;
import com.gym.entities.PurchaseDetail;
import com.gym.entities.StoreSubscription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDTO {

    private Long id;
    private StoreSubscription storeSubscription;
    private List<PurchaseDetail> purchaseDetails;
    private List<Coupon> couponsApplied;
    private LocalDate purchaseDate;
    private Account account;
}
