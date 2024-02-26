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

    @OneToMany(targetEntity = Product.class, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "purchase")
    private List<Product> productList;

//    @OneToMany(targetEntity = Subscription.class, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "purchase")
//    private List<Subscription> subscriptionList;

    @ManyToOne(targetEntity = StoreSubscription.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "store_subscription_id")
    private StoreSubscription storeSubscription;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_purchase_coupons", joinColumns = @JoinColumn(name = "purchase_id"), inverseJoinColumns = @JoinColumn(name = "coupon_id"))
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
