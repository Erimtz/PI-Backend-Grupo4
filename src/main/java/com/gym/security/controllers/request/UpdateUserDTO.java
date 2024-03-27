package com.gym.security.controllers.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {

    @Email
    @NotNull
    @NotBlank
    @Size(max = 100)
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 100)
    private String firstName;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 100)
    private String lastName;

}