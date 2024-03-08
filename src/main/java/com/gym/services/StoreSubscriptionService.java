package com.gym.services;

import com.gym.dto.request.StoreSubscriptionCreateDTO;
import com.gym.dto.response.StoreSubscriptionResponseDTO;
import com.gym.dto.request.StoreSubscriptionUpdateDTO;
import com.gym.entities.StoreSubscription;

import java.util.List;

public interface StoreSubscriptionService {

    List<StoreSubscriptionResponseDTO> getAllStoreSubscriptions();
    StoreSubscriptionResponseDTO getStoreSubscriptionById(Long id);
    StoreSubscriptionResponseDTO createStoreSubscription(StoreSubscriptionCreateDTO storeSubscriptionCreateDTO);
    StoreSubscriptionResponseDTO updateStoreSubscription(StoreSubscriptionUpdateDTO storeSubscriptionUpdateDTO);
    void deleteStoreSubscriptionById(Long id);
    public StoreSubscription convertToEntity(StoreSubscriptionResponseDTO storeSubscriptionResponseDTO);
}
