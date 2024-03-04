package com.gym.services;

import com.gym.dto.SubscriptionDTO;
import com.gym.entities.Account;
import com.gym.entities.StoreSubscription;
import com.gym.entities.Subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionService {

    List<Subscription> getAllSubscriptions();
    List<Subscription> getAllExpiredSubscriptions();
    List<Subscription> getAllActiveSubscriptions();
    Optional<Subscription> getSubscriptionById(Long id);
    Optional<Subscription> getSubscriptionByAccountId(Long id);
    Subscription createSubscription(Account account);
    Subscription updateSubscription(SubscriptionDTO subscriptionDTO);
    Subscription updateSubscriptionPurchase(StoreSubscription storeSubscription, String token);
    void deleteSubscriptionById(Long id);
}
