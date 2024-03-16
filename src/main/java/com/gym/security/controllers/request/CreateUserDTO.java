package com.gym.security.controllers.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDTO {
    @Email
    @NotBlank
    private String email;
    @Min(3)
    private String firstName;
    @Min(3)
    private String lastName;
    @NotBlank
    @Min(3)
    private String username;
    @NotBlank
    @Min(6)
    private String password;
    private Set<String> roles;
    private String document;
}
