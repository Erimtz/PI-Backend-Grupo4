package com.gym.security.controllers;

import com.gym.mail.services.IEmailService;
import com.gym.security.controllers.request.ResetPasswordDTO;
import com.gym.security.entities.PasswordResetToken;
import com.gym.security.entities.UserEntity;
import com.gym.security.repositories.UserRepository;
import com.gym.security.services.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/password-reset")
public class PasswordResetTokenController {

    @Autowired
    private PasswordResetTokenService tokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String userEmail) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("No se encontró ningún usuario con este correo electrónico");
        }
        UserEntity user = optionalUser.get();

        String token = generateToken();

        tokenService.createPasswordResetTokenForUser(user, token);

        sendResetPasswordEmail(userEmail, token);

        return ResponseEntity.ok("Se ha enviado un enlace de restablecimiento de contraseña a su correo electrónico");
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {

        String token = resetPasswordDTO.getTokenPassword();
        String password = resetPasswordDTO.getPassword();
        String confirmPassword = resetPasswordDTO.getConfirmPassword();

        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("La nueva contraseña y la confirmación de la contraseña no coinciden");
        }

        if (token == null || token.isEmpty() || password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body("El token y la contraseña son obligatorios");
        }

        PasswordResetToken passwordResetToken = tokenService.getPasswordResetToken(token);

        if (passwordResetToken.getExpiryDate().isBefore(Instant.now())) {
            return ResponseEntity.badRequest().body("El token de restablecimiento de contraseña ha expirado");
        }

        String encodedPassword = passwordEncoder.encode(password);

        UserEntity user = passwordResetToken.getUser();
        user.setPassword(encodedPassword);
        userRepository.save(user);

        tokenService.deletePasswordResetToken(passwordResetToken);

        return ResponseEntity.ok("La contraseña se ha restablecido con éxito");
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private void sendResetPasswordEmail(String userEmail, String token) {
        String resetPasswordLink = "http://www.lightweightgym.com/password-reset?token=" + token;
        String message = "Hola,\n\nPara restablecer tu contraseña, haz clic en el siguiente enlace:\n" + resetPasswordLink;
        emailService.sendEmail(new String[]{userEmail}, "Restablecer Contraseña", message);
    }
}