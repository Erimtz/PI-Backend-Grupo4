package com.gym.security.controllers.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDTO {

    @NotBlank
    private String token;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
}