package com.gym.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JsonIgnore
    private Account account;

//    @Override
//    public String toString() {
//        return "Purchase{" +
//                "id=" + id +
//                ", purchaseDetails=" + purchaseDetails +
//                ", storeSubscription=" + storeSubscription +
//                ", purchaseDate=" + purchaseDate +
//                ", couponsApplied=" + couponsApplied +
//                '}';
//    }
    @Override
    public String toString() {
        return "Purchase{" +
                "id=" + id +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}
