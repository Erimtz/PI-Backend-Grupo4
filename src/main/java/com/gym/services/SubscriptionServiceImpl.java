package com.gym.services;

import com.gym.dto.SubscriptionDTO;
import com.gym.entities.Account;
import com.gym.entities.Subscription;
import com.gym.repositories.SubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
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
    public void deleteSubscriptionById(Long id) {
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findById(id);
        if (subscriptionOptional.isPresent()) {
            subscriptionRepository.deleteById(id);
        }
        throw new NoSuchElementException("Subscription with ID " + id + " not found");
    }
}
