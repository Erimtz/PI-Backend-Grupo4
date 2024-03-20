package com.gym.services.impl;

import com.gym.dto.AccountDTO;
import com.gym.dto.response.AccountDetailsDTO;
import com.gym.dto.response.AccountPurchaseDTO;
import com.gym.dto.response.PurchaseResponseDTO;
import com.gym.entities.Account;
import com.gym.entities.Purchase;
import com.gym.enums.ERank;
import com.gym.entities.Rank;
import com.gym.entities.Subscription;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.exceptions.UnauthorizedException;
import com.gym.exceptions.UserNotFoundException;
import com.gym.repositories.AccountRepository;
import com.gym.repositories.RankRepository;
import com.gym.security.configuration.jwt.JwtUtils;
import com.gym.security.entities.UserEntity;
import com.gym.security.repositories.UserRepository;
import com.gym.services.PurchaseService;
import com.gym.services.SubscriptionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    @Autowired
    @Lazy
    private PurchaseService purchaseService;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(UserEntity user, String document) {
        Optional<Rank> accountRankOptional = rankRepository.findByName(ERank.BRONZE);

        Rank rankAccount = accountRankOptional.orElseGet(() -> {
            Rank newAccountRank = Rank.builder()
                    .name(ERank.BRONZE)
                    .build();
            return rankRepository.save(newAccountRank);
        });
        Account account = Account.builder()
                .user(user)
                .document(document)
                .transferList(new ArrayList<>())
                .couponList(new ArrayList<>())
                .purchaseList(new ArrayList<>())
                .creditBalance(BigDecimal.valueOf(1000.0))
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

    public AccountDetailsDTO getAccountDetailsById(Long id){
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            AccountDetailsDTO accountDetailsDTO = AccountDetailsDTO.builder()
                    .id(account.getId())
                    .userId(account.getUser().getId())
                    .document(account.getDocument())
                    .creditBalance(account.getCreditBalance())
                    .rank(account.getRank().getName().name())
                    .build();
            return accountDetailsDTO;
        } else {
            throw new ResourceNotFoundException("Account not found with ID: " + id);
        }
    }

    public Account getAccountById(Long id){
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            return account;
        } else {
            throw new ResourceNotFoundException("Account not found with ID: " + id);
        }
    }

    public BigDecimal getAccountCreditBalance(Account account) {
        Boolean accountExists = accountRepository.existsById(account.getId());
        if (accountExists) {
            AccountDetailsDTO accountDTO = getAccountDetailsById(account.getId());
            BigDecimal creditBalance = accountDTO.getCreditBalance();
            return creditBalance;
        } else {
            throw new IllegalArgumentException("Account not found");
        }
    }

    public void sustractFromCreditBalance(Account account, BigDecimal totalPurchaseAmount){
        Optional<Account> optionalAccount = accountRepository.findById(account.getId());
        if (optionalAccount.isPresent()) {
            Account accountEntity = optionalAccount.get();
            BigDecimal newCreditBalance = accountEntity.getCreditBalance().subtract(totalPurchaseAmount);
            accountEntity.setCreditBalance(newCreditBalance);
            accountRepository.save(accountEntity);
        } else {
            throw new IllegalArgumentException("Account not found");
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

    @Transactional
    public void updateSubscription(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }
        accountRepository.save(account);
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



    public List<AccountPurchaseDTO> getAllAccountsWithPurchasesDTO() {
        Iterable<Account> accountIterable = accountRepository.findAll();
        List<Account> accounts = new ArrayList<>();
        accountIterable.forEach(accounts::add);
        return accounts.stream()
                .map(this::mapToAccountPurchaseDTO)
                .collect(Collectors.toList());
    }

    private AccountPurchaseDTO mapToAccountPurchaseDTO(Account account) {
        return new AccountPurchaseDTO(
                account.getId(),
                account.getUser().getId(),
                account.getCreditBalance(),
                account.getRank().getName().name(),
                mapPurchasesToDTO(account.getPurchaseList())
        );
    }

    private List<PurchaseResponseDTO> mapPurchasesToDTO(List<Purchase> purchases) {
        return purchases.stream()
                .map(this::mapToPurchaseDTO)
                .collect(Collectors.toList());
    }

    private PurchaseResponseDTO mapToPurchaseDTO(Purchase purchase) {
        return purchaseService.buildPurchaseResponse(purchase);
    }

    public long count() {
        return userRepository.count();
    }
}
