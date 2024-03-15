package com.gym.services.impl;

import com.gym.dto.SubscriptionDTO;
import com.gym.entities.Account;
import com.gym.entities.StoreSubscription;
import com.gym.entities.Subscription;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.SubscriptionRepository;
import com.gym.security.repositories.UserRepository;
import com.gym.services.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    @Lazy
    private AccountService accountService;
    @Autowired
    private UserRepository userRepository;

    public List<Subscription> getAllSubscriptions(){
        return subscriptionRepository.findAll();
    }

    @Override
    public List<Subscription> getAllExpiredSubscriptions() {
        return subscriptionRepository.findExpiredSubscriptions();
    }

    @Override
    public List<Subscription> getAllActiveSubscriptions() {
        return subscriptionRepository.findActiveSubscriptions();
    }

    @Override
    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    @Override
    public Optional<Subscription> getSubscriptionByAccountId(Long id) {
        return subscriptionRepository.findByAccountId(id);
    }

    @Transactional
    @Override
    public Subscription createSubscription(Account account) {
        Subscription subscription = Subscription.builder()
                .name("No subscription")
                .price(0.00)
                .imageUrl("Direccion imagen sin suscripción")
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().minusDays(1))
                .planType("None")
                .automaticRenewal(false)
                .account(account)
                .build();

        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public Subscription updateSubscription(SubscriptionDTO subscriptionDTO) {
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findById(subscriptionDTO.getId());
        if (subscriptionOptional.isPresent()) {
            Subscription subscription = subscriptionOptional.get();

            if (subscriptionDTO.getName() != null && !subscriptionDTO.getName().isEmpty()) {
                subscription.setName(subscriptionDTO.getName());
            }
            if (subscriptionDTO.getPrice() != null) {
                subscription.setPrice(subscriptionDTO.getPrice());
            }
            if (subscriptionDTO.getImageUrl() != null && !subscriptionDTO.getImageUrl().isEmpty()) {
                subscription.setImageUrl(subscriptionDTO.getImageUrl());
            }
            if (subscriptionDTO.getStartDate() != null) {
                subscription.setStartDate(subscriptionDTO.getStartDate());
            }
            if (subscriptionDTO.getEndDate() != null) {
                subscription.setEndDate(subscriptionDTO.getEndDate());
            }
            if (subscriptionDTO.getPlanType() != null && !subscriptionDTO.getPlanType().isEmpty()) {
                subscription.setPlanType(subscriptionDTO.getPlanType());
            }
            if (subscriptionDTO.getAutomaticRenewal() != null){
                subscription.setAutomaticRenewal(subscriptionDTO.getAutomaticRenewal());
            }

            return subscriptionRepository.save(subscription);
        } else {
            throw new NoSuchElementException("Subscription with ID " + subscriptionDTO.getId() + " not found");
        }
    }

    @Override
    @Transactional
    public Subscription updateSubscriptionPurchase(StoreSubscription storeSubscription, String token) {
        Account account = accountService.getAccountFromToken(token);
        if (account == null) {
            throw new IllegalArgumentException("No se pudo obtener la cuenta del usuario");
        }
        Subscription subscription = subscriptionRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se pudo obtener la suscripcion con ID: " + account.getId()));

            subscription.setName(storeSubscription.getName());
            subscription.setPrice(storeSubscription.getPrice());
            subscription.setImageUrl(storeSubscription.getImageUrl());
            subscription.setStartDate(LocalDate.now());
            subscription.setEndDate(LocalDate.now().plusDays(storeSubscription.getDurationDays()));
            subscription.setPlanType(storeSubscription.getPlanType());
            subscription.setAutomaticRenewal(subscription.getAutomaticRenewal());

            return subscriptionRepository.save(subscription);
    }

    @Override
    public double calculateActiveSubscriptionRatio() {
        List<Subscription> activeSubscriptions = subscriptionRepository.findActiveSubscriptions();
        long totalUsers = userRepository.count();

        if (totalUsers == 0) {
            return 0.0; // Evitar la división por cero
        }

        int activeSubscriptionCount = activeSubscriptions.size();
        return (double) activeSubscriptionCount / totalUsers;
    }
    @Override
    public void deleteSubscriptionById(Long id) {
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findById(id);
        if (subscriptionOptional.isPresent()) {
            subscriptionRepository.deleteById(id);
        }
        throw new NoSuchElementException("Subscription with ID " + id + " not found");
    }
}
