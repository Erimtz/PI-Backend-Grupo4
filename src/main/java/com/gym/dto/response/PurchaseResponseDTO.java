package com.gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponseDTO {
    private Long id;
    private LocalDate purchaseDate;
    private List<PurchaseDetailResponseDTO> purchaseDetails;
    private Double subscriptionPrice;
    private Double total;
    private List<CouponResponseDTO> couponsResponseDTO;
    private Double discount;
    private Double totalAfterDiscounts;
}