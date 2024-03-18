package com.gym.security.controllers.request;

//import jakarta.validation.constraints.*;
//import javax.validation.constraints.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull
    private String email;
    @Size(min = 3, max = 30)
    @NotBlank
    @NotNull
    private String firstName;
    @Size(min = 3, max = 30)
    private String lastName;
    @NotBlank
    @NotNull
    @Size(min = 3, max = 20)
    private String username;
    @NotBlank
    @NotNull
    @Size(min = 6, max = 64)
    private String password;
    private Set<String> roles;
    @NotNull
    private String document;
}
