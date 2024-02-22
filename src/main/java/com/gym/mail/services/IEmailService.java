package com.gym.mail.services;

import com.gym.mail.domain.EmailValuesDTO;

import java.io.File;

public interface IEmailService {

    void sendEmail(String[] toUsers, String subject, String message);
    void sendEmailWithFile(String[] toUsers, String subject, String message, File file);

    void sendEmailTemplate(EmailValuesDTO dto);
}
