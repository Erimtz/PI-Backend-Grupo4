package com.gym.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    private Double amount;
    private Boolean spent;

    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

    @ManyToOne(targetEntity = Purchase.class)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;
}
