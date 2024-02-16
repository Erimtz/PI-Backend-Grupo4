package com.gym.security.services;

import com.gym.security.entities.PasswordResetToken;
import com.gym.security.entities.UserEntity;
import com.gym.security.repositories.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    public void createPasswordResetTokenForUser(UserEntity user, String token) {
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        myToken.setExpiryDate(Instant.now().plusSeconds(86400));
        tokenRepository.save(myToken);
    }

    public PasswordResetToken getPasswordResetToken(String token) {
        return tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token no encontrado"));
    }

    public void deletePasswordResetToken(PasswordResetToken token) {
        tokenRepository.delete(token);
    }
}