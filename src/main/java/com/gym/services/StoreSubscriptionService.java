package com.gym.services;

import com.gym.dto.CreateStoreSubscriptionDTO;
import com.gym.dto.ResponseStoreSubscription;
import com.gym.dto.UpdateStoreSubscriptionDTO;
import com.gym.entities.StoreSubscription;

import java.util.List;
import java.util.Optional;

public interface StoreSubscriptionService {

    List<ResponseStoreSubscription> getAllStoreSubscriptions();
    ResponseStoreSubscription getStoreSubscriptionById(Long id);
    ResponseStoreSubscription createStoreSubscription(CreateStoreSubscriptionDTO createSubscriptionDTO);
    ResponseStoreSubscription updateStoreSubscription(UpdateStoreSubscriptionDTO updateStoreSubscriptionDTO);
    void deleteStoreSubscriptionById(Long id);
}
