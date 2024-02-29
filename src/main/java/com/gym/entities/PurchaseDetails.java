package com.gym.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "purchase_details")
public class PurchaseDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "purchase_id")
        private Purchase purchase;

        @ManyToOne
        @JoinColumn(name = "product_id")
        private Product product;

        private int quantity;

        // Método para calcular el valor total de este detalle de compra
        public double getTotalValue() {
                return quantity * product.getPrice();
        }

}
