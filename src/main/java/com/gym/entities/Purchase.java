package com.gym.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "purchase")
    private List<PurchaseDetail> purchaseDetails;

    @ManyToOne(targetEntity = StoreSubscription.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "store_subscription_id")
    private StoreSubscription storeSubscription;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private List<Coupon> couponsApplied;

    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "account_id")
    private Account account;

    // ESTO ES UNA BURRADA PROVISORIA PARA QUE NO DE ERROR //////////////////
    /////////////////////////////////////////////////////////////////////////
    public BigDecimal calculateTotalAmount(){
        return new BigDecimal(1);
    }
    /////////////////////////////////////////////////////////////////////////
}
