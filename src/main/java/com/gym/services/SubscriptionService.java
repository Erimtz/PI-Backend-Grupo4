package com.gym.services;

import com.gym.dto.SubscriptionDTO;
import com.gym.dto.response.SubscriptionResponseDTO;
import com.gym.entities.Account;
import com.gym.entities.StoreSubscription;
import com.gym.entities.Subscription;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

public interface SubscriptionService {

    List<SubscriptionResponseDTO> getAllSubscriptions();
    List<SubscriptionResponseDTO> getAllExpiredSubscriptions();
    List<SubscriptionResponseDTO> getAllActiveSubscriptions();
    SubscriptionResponseDTO getSubscriptionById(Long id);
    Optional<Subscription> getSubscriptionByAccountId(Long id);
    Subscription createSubscription(Account account);
    Subscription updateSubscription(SubscriptionDTO subscriptionDTO);
    Subscription updateAutomaticRenewal(Long accountId, boolean automaticRenewal, HttpServletRequest request);
    List<SubscriptionResponseDTO> renewExpiredSubscriptions(String token);
    Subscription updateSubscriptionPurchase(StoreSubscription storeSubscription, String token);
    void deleteSubscriptionById(Long id);
}
