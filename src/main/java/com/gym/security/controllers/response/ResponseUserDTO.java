package com.gym.security.controllers.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserDTO {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Long accountId;
    private String rol;
}
