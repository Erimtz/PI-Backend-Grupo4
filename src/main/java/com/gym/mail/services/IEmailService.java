package com.gym.mail.services;

import java.io.File;

public interface IEmailService {

    void sendEmail(String[] toUsers, String subject, String message);
    void SendEmailWithFile(String[] toUsers, String subject, String message, File file);
}
