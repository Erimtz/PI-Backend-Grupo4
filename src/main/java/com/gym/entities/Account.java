package com.gym.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "credit_balance")
    private BigDecimal creditBalance;

    @ManyToOne(targetEntity = Rank.class)
    @JoinColumn(name = "rank_id")
    private Rank rank;

    @OneToMany(targetEntity = Coupon.class ,fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "account")
    private List<Coupon> couponList;

    @OneToMany(targetEntity = Purchase.class ,fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "account")
    private List<Purchase> purchaseList;

    @OneToMany(targetEntity = Transfer.class ,fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "account")
    private List<Transfer> transferList;

    @OneToMany(targetEntity = PaymentMethod.class ,fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "account")
    private List<PaymentMethod> paymentMethodSet;
}
