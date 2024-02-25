package com.gym.services;

import com.gym.dto.AccountDTO;
import com.gym.entities.Account;
import com.gym.entities.ERank;
import com.gym.entities.Rank;
import com.gym.entities.Subscription;
import com.gym.repositories.AccountRepository;
import com.gym.repositories.RankRepository;
import com.gym.security.entities.UserEntity;
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

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

//    public Account createAccount(UserEntity user, BigDecimal creditBalance, Rank rank) {
//        Account account = new Account( user, creditBalance, rank);
//        accountRepository.save(account);
//        return account;
//
//    }

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

//        Subscription subscription = Subscription.builder()
//                .name("No subscription")
//                .price(0.00)
//                .imageUrl("Direccion imagen sin suscripción")
//                .startDate(LocalDate.now())
//                .endDate(LocalDate.now())
//                .planType("None")
//                .automaticRenewal(false)
//                .account(account)
//                .build();
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

}
