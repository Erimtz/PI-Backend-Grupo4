package com.gym.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PurchaseRequestDTO {

    private List<PurchaseDetailRequestDTO> purchaseDetails;
    private List<Long> couponsIds;
    private Long storeSubscriptionId;

    public PurchaseRequestDTO(List<PurchaseDetailRequestDTO> purchaseDetails, List<Long> couponsIds, Long storeSubscriptionId) {
        this.purchaseDetails = purchaseDetails;
        this.couponsIds = couponsIds != null ? couponsIds : new ArrayList<>();
        this.storeSubscriptionId = storeSubscriptionId;
    }
}
