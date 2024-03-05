package com.gym.mail.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailValuesDTO {

    private String mailFrom;
    private String mailTo;
    private String subject;
    private String username;
    private String firstName;
    private String lastName;
    private String tokenPassword;
}
