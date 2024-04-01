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
            throw new UnauthorizedException("No authorization token found.");
        }

        String tokenUsername = jwtUtils.getUsernameFromToken(token);
        if (tokenUsername == null) {
            throw new UnauthorizedException("Could not retrieve the username from the token.");
        }

        Optional<UserEntity> optionalUser = userRepository.findByUsername(tokenUsername);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found.");
        }

        UserEntity user = optionalUser.get();
        return accountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found for the user: " + user.getId()));
    }

    public boolean isAdminFromToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null) {
            throw new UnauthorizedException("No authorization token found.");
        }

        String tokenUsername = jwtUtils.getUsernameFromToken(token);
        if (tokenUsername == null) {
            throw new UnauthorizedException("Could not retrieve the username from the token.");
        }

        Optional<UserEntity> optionalUser = userRepository.findByUsername(tokenUsername);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found.");
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
            return true; // Administrators always have access
        }

        Account authenticatedAccount = getAccountFromToken(request);
        if (authenticatedAccount != null && authenticatedAccount.getId().equals(accountId)) {
            return true; // Authenticated users have access only to their own account
        }

        return false; // The authenticated user does not have access to the specified account, especially if not authenticated.
    }
}