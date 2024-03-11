package com.gym.mail.services;

import com.gym.mail.domain.EmailValuesDTO;
import com.gym.security.controllers.response.ResponseUserDTO;

import java.io.File;

public interface EmailService {

    void sendEmail(String[] toUsers, String subject, String message);
    void sendEmailWithFile(String[] toUsers, String subject, String message, File file);

    void sendEmailTemplate(EmailValuesDTO dto);

    void sendEmailNewUser(ResponseUserDTO dto);
}
