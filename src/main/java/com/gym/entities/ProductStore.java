package com.gym.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "store_products")
public class ProductStore{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
    private String description;
    private Long stock;

    @ManyToOne(targetEntity = Purchase.class)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;
}
