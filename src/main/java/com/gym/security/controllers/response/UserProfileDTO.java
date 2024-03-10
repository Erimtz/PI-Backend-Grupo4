package com.gym.security.controllers.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

    private Long id;
    private String username;
    private String rol;
    private String firstName;
    private String lastName;
    private String document;
    private String email;
    private Long accountId;
    private String rank;
    private BigDecimal creditBalance;
    private Long subscriptionId;
    private String subscription;
    private String planType;
    private Boolean isExpired;

}