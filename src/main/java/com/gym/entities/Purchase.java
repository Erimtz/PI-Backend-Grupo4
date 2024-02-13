package com.gym.entities;

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
    // Mi compra va a tener un id de compra, va a tener un usuario que realizo la compra, va a tener una lista de productos comprados, va a tener una lista de cupones que utilicé para descontar del total de la compra

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(targetEntity = ProductStore.class, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "purchase")
    private List<ProductStore> productList;

    @OneToMany(targetEntity = Subscription.class, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "purchase")
    private List<Subscription> subscriptionList;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "user_purchase_coupons", joinColumns = @JoinColumn(name = "purchase_id"), inverseJoinColumns = @JoinColumn(name = "coupon_id"))
//    private List<Coupon> couponsApplied;

    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "account_id")
    private Account account;
}
