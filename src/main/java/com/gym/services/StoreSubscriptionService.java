package com.gym.services;

import com.gym.dto.CreateStoreSubscriptionDTO;
import com.gym.dto.ResponseStoreSubscription;
import com.gym.dto.UpdateStoreSubscriptionDTO;
import com.gym.entities.StoreSubscription;

import java.util.List;

public interface StoreSubscriptionService {

    List<ResponseStoreSubscription> getAllStoreSubscriptions();
    ResponseStoreSubscription getStoreSubscriptionById(Long id);
    ResponseStoreSubscription createStoreSubscription(CreateStoreSubscriptionDTO createStoreSubscriptionDTO);
    ResponseStoreSubscription updateStoreSubscription(UpdateStoreSubscriptionDTO updateStoreSubscriptionDTO);
    void deleteStoreSubscriptionById(Long id);
    public StoreSubscription convertToEntity(ResponseStoreSubscription responseStoreSubscription);
}
