package com.gym.service;

import com.gym.dto.AccountDto;
import com.gym.entities.Account;
import com.gym.entities.Rank;
import com.gym.repositories.AccountRepository;
import com.gym.security.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountService {
    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(UserEntity user, BigDecimal creditBalance, Rank rank) {
        Account account = new Account( user, creditBalance, rank);
        return accountRepository.save(account);

    }

    public AccountDto getAccountDetails(String username) {
        Optional<Account> optionalAccount = accountRepository.findByUserUsername(username);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            UserEntity user = account.getUser();
            return new AccountDto(account.getId(), user.getUsername());
        } else {
            throw new IllegalArgumentException("Account not found with ID: " + username);
        }
    }

    public void deleteAccountByUserId(Long userId) {
        // Eliminar una cuenta por el ID del usuario asociado
        Optional<Account> optionalAccount = accountRepository.findByUserId(userId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            accountRepository.delete(account);
        } else {
            throw new IllegalArgumentException("Account not found for user with ID: " + userId);
        }
    }

}
