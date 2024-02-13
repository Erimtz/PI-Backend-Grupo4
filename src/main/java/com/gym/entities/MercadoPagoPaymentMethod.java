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
@Table(name = "mercadopago_payments")
public class MercadoPagoPaymentMethod extends PaymentMethod{

    @Column(name = "email")
    private String email;

}
