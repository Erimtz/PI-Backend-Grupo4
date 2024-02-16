//package com.gym.entities;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Inheritance(strategy = InheritanceType.JOINED)
//public abstract class PaymentMethod {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "owner_name")
//    private String ownerName;
//
//    @Column(name = "owner_email")
//    private String ownerEmail;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "account_id")
//    private Account account;
//}
//
