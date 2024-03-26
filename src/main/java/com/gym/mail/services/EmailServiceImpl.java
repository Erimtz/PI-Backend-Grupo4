package com.gym.mail.services;


import com.gym.dto.response.PurchaseResponseDTO;
import com.gym.exceptions.EmailNotRegisteredException;
import com.gym.exceptions.InvalidTokenException;
import com.gym.exceptions.UserNotFoundException;
import com.gym.mail.domain.EmailValuesDTO;
import com.gym.security.configuration.jwt.JwtUtils;
import com.gym.security.controllers.response.ResponseUserDTO;
import com.gym.security.entities.UserEntity;
import com.gym.security.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String emailAccount;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void sendEmail(String[] toUsers, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailAccount);
        mailMessage.setTo(toUsers);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    @Override
    public void sendEmailWithFile(String[] toUsers, String subject, String message, File file) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(emailAccount);
            mimeMessageHelper.setTo(toUsers);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message);
            mimeMessageHelper.addAttachment(file.getName(), file);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Value("${mail.urlFront}")
    private String urlFront;

    public void sendEmailTemplate(EmailValuesDTO dto) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("username", dto.getUsername());
            model.put("firstName", dto.getFirstName());
            model.put("lastName", dto.getLastName());
            model.put("url", urlFront + dto.getTokenPassword());
            context.setVariables(model);
            String htmlText = templateEngine.process("email-template", context);
            helper.setFrom(dto.getMailFrom());
            helper.setTo(dto.getMailTo());
            helper.setSubject(dto.getSubject());
            helper.setText(htmlText, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendEmailNewUser(ResponseUserDTO dto) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
//            model.put("username", dto.getUsername());
            model.put("firstName", dto.getFirstName());
            model.put("lastName", dto.getLastName());
//            model.put("url", urlFront + dto.getTokenPassword());
            context.setVariables(model);
            String htmlText = templateEngine.process("email-new-user", context);
            helper.setFrom(emailAccount);
            helper.setTo(dto.getEmail());
            helper.setSubject("¡Bienvenido a Lightweight! Tu cuenta está lista para empezar");
            helper.setText(htmlText, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPurchaseConfirmationEmail(PurchaseResponseDTO dto, String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtUtils.getUsernameFromToken(token);
        if (username != null) {
            Optional<UserEntity> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                UserEntity userEntity = userOptional.get();
                String email = userEntity.getEmail();
                String firstName = userEntity.getFirstName();
                String lastName = userEntity.getLastName();
                if (email != null) {
                    MimeMessage message = mailSender.createMimeMessage();
                    try {
                        MimeMessageHelper helper = new MimeMessageHelper(message, true);
                        Context context = new Context();
                        Map<String, Object> model = new HashMap<>();
                        model.put("firstName", firstName);
                        model.put("lastName", lastName);
                        model.put("purchase", dto);
                        context.setVariables(model);
                        String htmlText = templateEngine.process("email-purchase", context);
                        helper.setFrom(emailAccount);
                        helper.setTo(email);
                        helper.setSubject("Confirmación de compra en Lightweight");
                        helper.setText(htmlText, true);
                        mailSender.send(message);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new EmailNotRegisteredException("El usuario no tiene un correo electrónico registrado.");
                }
            } else {
                throw new UserNotFoundException("El usuario no se encontró en la base de datos.");
            }
        } else {
            throw new InvalidTokenException("El token JWT no es válido.");
        }
    }
}
