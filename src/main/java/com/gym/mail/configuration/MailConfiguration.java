package com.gym.mail.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


import java.util.Properties;

@Configuration
public class MailConfiguration {

    @Value("${spring.mail.username}")
    private String emailAccount;
    @Value("${spring.mail.password}")
    private String emailPassword;
    @Value("${spring.mail.host}")
    private String emailHost;
    @Value("${spring.mail.port}")
    private String emailPort;
    @Value("${spring.mail.properties.smtp.auth}")
    private String emailSmtpAuth;
    @Value("${spring.mail.properties.smtp.starttls.enable}")
    private String emailStartTlsEnable;

    @Bean
    public JavaMailSender getJavaMailSender (){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(emailHost);
        mailSender.setPort(Integer.parseInt(emailPort));
        mailSender.setUsername(emailAccount);
        mailSender.setPassword(emailPassword);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", emailSmtpAuth);
        props.put("mail.smtp.starttls.enable", emailStartTlsEnable);
        props.put("mail.debug", "true");

        return mailSender;
    }
}
