package com.gym.dto.request;

import com.gym.entities.Account;
import com.gym.entities.Coupon;
import com.gym.entities.PurchaseDetail;
import com.gym.entities.StoreSubscription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PurchaseRequestDTO {

    private List<PurchaseDetailRequestDTO> purchaseDetails;
//    private List<Coupon> couponsApplied;
    private List<Long> couponsIds;
    private Long storeSubscriptionId;

    public PurchaseRequestDTO(List<PurchaseDetailRequestDTO> purchaseDetails, List<Long> couponsIds, Long storeSubscriptionId) {
        this.purchaseDetails = purchaseDetails;
        this.couponsIds = couponsIds != null ? couponsIds : new ArrayList<>(); // Si couponsIds es null, inicializar con una lista vacía
        this.storeSubscriptionId = storeSubscriptionId;
    }
}
