package com.gym.security.controllers.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    @Email
    @NonNull
    private String email;
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;

}