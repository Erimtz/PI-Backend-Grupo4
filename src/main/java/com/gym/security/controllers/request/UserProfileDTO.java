package com.gym.security.controllers.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles;

}