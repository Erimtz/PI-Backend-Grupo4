package com.gym.security.configuration.utils;

import com.gym.entities.Account;
import com.gym.enums.ERole;
import com.gym.exceptions.UnauthorizedException;
import com.gym.exceptions.UserNotFoundException;
import com.gym.repositories.AccountRepository;
import com.gym.security.configuration.jwt.JwtUtils;
import com.gym.security.entities.UserEntity;
import com.gym.security.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccountTokenUtils {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public AccountTokenUtils(JwtUtils jwtUtils, UserRepository userRepository, AccountRepository accountRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    public Account getAccountFromToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null) {
            throw new UnauthorizedException("No se encontró un token de autorización.");
        }

        String tokenUsername = jwtUtils.getUsernameFromToken(token);
        if (tokenUsername == null) {
            throw new UnauthorizedException("No se pudo obtener el nombre de usuario del token.");
        }

        Optional<UserEntity> optionalUser = userRepository.findByUsername(tokenUsername);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        UserEntity user = optionalUser.get();
        return accountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada para el usuario: " + user.getId()));
    }

    public boolean isAdminFromToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null) {
            throw new UnauthorizedException("No se encontró un token de autorización.");
        }

        String tokenUsername = jwtUtils.getUsernameFromToken(token);
        if (tokenUsername == null) {
            throw new UnauthorizedException("No se pudo obtener el nombre de usuario del token.");
        }

        Optional<UserEntity> optionalUser = userRepository.findByUsername(tokenUsername);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        UserEntity user = optionalUser.get();
        return user.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ADMIN));
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            return tokenHeader.substring(7);
        }
        return null;
    }

    public boolean hasAccessToAccount(HttpServletRequest request, Long accountId) {
        boolean isAdmin = isAdminFromToken(request);
        if (isAdmin) {
            return true; // Los administradores siempre tienen acceso
        }

        Account authenticatedAccount = getAccountFromToken(request);
        if (authenticatedAccount != null && authenticatedAccount.getId().equals(accountId)) {
            return true; // El usuario autenticado tiene acceso solo a su propia cuenta
        }

        return false; // El usuario autenticado no tiene acceso a la cuenta especificada, mucho menos si no está autenticado
    }
}