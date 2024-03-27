package com.gym.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gym.security.entities.UserEntity;
import jakarta.persistence.*;
import lombok.*;
//import org.springframework.security.core.userdetails.User;

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

    private String document;

    @OneToOne
    @JoinColumn(name = "user_id")
//    @ToString.Exclude
    private UserEntity user;

    @Column(name = "credit_balance")
    private BigDecimal creditBalance;

    @ManyToOne(targetEntity = Rank.class)
    @JoinColumn(name = "rank_id")
    private Rank rank;

    @OneToMany(targetEntity = Coupon.class ,fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "account")
    private List<Coupon> couponList;

    @OneToMany(targetEntity = Purchase.class ,fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "account")
    @JsonIgnore
    private List<Purchase> purchaseList;

    @OneToMany(targetEntity = Transfer.class ,fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "account")
    private List<Transfer> transferList;

    public Account(UserEntity user, BigDecimal creditBalance, Rank rank) {
        this.user = user;
        this.creditBalance = creditBalance;
        this.rank = rank;
    }

    public Account(UserEntity user, BigDecimal creditBalance, Rank rank, List<Coupon> couponList, List<Purchase> purchaseList, List<Transfer> transferList) {
        this.user = user;
        this.creditBalance = creditBalance;
        this.rank = rank;
        this.couponList = couponList;
        this.purchaseList = purchaseList;
        this.transferList = transferList;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", document='" + document + '\'' +
                ", user=" + user +
                ", creditBalance=" + creditBalance +
                ", rank=" + rank +
                ", couponList=" + couponList +
                ", transferList=" + transferList +
                '}';
    }


    //    @OneToMany(targetEntity = PaymentMethod.class ,fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "account")
//    private List<PaymentMethod> paymentMethodSet;
}
