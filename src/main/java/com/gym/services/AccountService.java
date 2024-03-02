package com.gym.services;

import com.gym.dto.AccountDTO;
import com.gym.entities.Account;
import com.gym.enums.ERank;
import com.gym.entities.Rank;
import com.gym.entities.Subscription;
import com.gym.exceptions.UnauthorizedException;
import com.gym.exceptions.UserNotFoundException;
import com.gym.repositories.AccountRepository;
import com.gym.repositories.RankRepository;
import com.gym.security.configuration.jwt.JwtUtils;
import com.gym.security.entities.UserEntity;
import com.gym.security.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class AccountService {
    private AccountRepository accountRepository;
    @Autowired
    private RankRepository rankRepository;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(UserEntity user) {
        Optional<Rank> accountRankOptional = rankRepository.findByName(ERank.BRONZE);

        Rank rankAccount = accountRankOptional.orElseGet(() -> {
            Rank newAccountRank = Rank.builder()
                    .name(ERank.BRONZE)
                    .build();
            return rankRepository.save(newAccountRank);
        });
        Account account = Account.builder()
                .user(user)
                .transferList(new ArrayList<>())
                .couponList(new ArrayList<>())
                .purchaseList(new ArrayList<>())
                .creditBalance(BigDecimal.valueOf(0.0))
                .rank(rankAccount)
                .build();
        accountRepository.save(account);
        subscriptionService.createSubscription(account);

        return account;
    }

    public AccountDTO getAccountDetails(String username) {
        Optional<Account> optionalAccount = accountRepository.findByUserUsername(username);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            UserEntity user = account.getUser();
            return new AccountDTO(account.getId(), user.getUsername());
        } else {
            throw new IllegalArgumentException("Account not found with ID: " + username);
        }
    }

    public void deleteAccountByUserId(Long userId) {
        Optional<Account> optionalAccount = accountRepository.findByUserId(userId);
        if (optionalAccount.isPresent()) {

            Account account = optionalAccount.get();

            Optional<Subscription> subscriptionOptional = subscriptionService.getSubscriptionByAccountId(account.getId());
            if (subscriptionOptional.isPresent()){
                Subscription subscription = subscriptionOptional.get();
                subscriptionService.deleteSubscriptionById(subscription.getId());
            }

            accountRepository.delete(account);
        } else {
            throw new IllegalArgumentException("Account not found for user with ID: " + userId);
        }
    }

    public Account getAccountFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("No se encontró un token de autorización válido.");
        }
        token = token.substring(7);

        String tokenUsername = jwtUtils.getUsernameFromToken(token);
        if (tokenUsername == null) {
            throw new UnauthorizedException("No se pudo obtener el nombre de usuario del token.");
        }
        Optional<UserEntity> optionalUser = userRepository.findByUsername(tokenUsername);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado");
        }
        UserEntity user = optionalUser.get();
        return accountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada para el usuario: " + user.getId()));
    }
}
