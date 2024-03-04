package com.gym.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "plan_type")
    private String planType;
    @Column(name = "automatic_renewal")
    private Boolean automaticRenewal;

    @OneToOne
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

//    @ManyToOne(targetEntity = Purchase.class)
//    @JoinColumn(name = "purchase_id")
//    private Purchase purchase;
}
