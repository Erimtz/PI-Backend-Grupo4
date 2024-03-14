package com.gym.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "store_subscriptions")
public class StoreSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
    private String description;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "plan_type")
    private String planType;
    @Column(name = "duration_days")
    private Integer durationDays;

    @OneToMany(mappedBy = "storeSubscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Purchase.class)
    @JsonIgnore
    private List<Purchase> purchases;

    @Override
    public String toString() {
        return "StoreSubscription{" +
                "id=" + id +
                ", price=" + price +
                '}';
    }
}
