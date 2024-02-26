package com.gym.services;

import com.gym.dto.CreateStoreSubscriptionDTO;
import com.gym.dto.ResponseStoreSubscription;
import com.gym.dto.UpdateStoreSubscriptionDTO;
import com.gym.entities.StoreSubscription;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.StoreSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreSubscriptionServiceImpl implements StoreSubscriptionService {

    private final StoreSubscriptionRepository storeSubscriptionRepository;

    @Override
    public List<ResponseStoreSubscription> getAllStoreSubscriptions() {
        return storeSubscriptionRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseStoreSubscription getStoreSubscriptionById(Long id) {
        return storeSubscriptionRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Store Subscription with ID " + id + " not found"));
    }

    @Override
    public ResponseStoreSubscription createStoreSubscription(CreateStoreSubscriptionDTO createStoreSubscriptionDTO) {
        try {
            StoreSubscription storeSubscription = StoreSubscription.builder()
                    .name(createStoreSubscriptionDTO.getName())
                    .price(createStoreSubscriptionDTO.getPrice())
                    .description(createStoreSubscriptionDTO.getDescription())
                    .imageUrl(createStoreSubscriptionDTO.getImageUrl())
                    .planType(createStoreSubscriptionDTO.getPlanType())
                    .purchases(new ArrayList<>())
                    .build();
            storeSubscriptionRepository.save(storeSubscription);
            return convertToDto(storeSubscription);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while saving store subscription", e);
        }
    }

    @Override
    public ResponseStoreSubscription updateStoreSubscription(UpdateStoreSubscriptionDTO updateStoreSubscriptionDTO) {
        Optional<StoreSubscription> storeSubscriptionOptional = storeSubscriptionRepository.findById(updateStoreSubscriptionDTO.getId());
        if (storeSubscriptionOptional.isPresent()){
            StoreSubscription storeSubscription = storeSubscriptionOptional.get();

            if (updateStoreSubscriptionDTO.getName() != null && !updateStoreSubscriptionDTO.getName().isEmpty()) {
                storeSubscription.setName(updateStoreSubscriptionDTO.getName());
            }
            if (updateStoreSubscriptionDTO.getPrice() != null) {
                storeSubscription.setPrice(updateStoreSubscriptionDTO.getPrice());
            }
            if (updateStoreSubscriptionDTO.getDescription() != null){
                storeSubscription.setDescription(updateStoreSubscriptionDTO.getDescription());
            }
            if (updateStoreSubscriptionDTO.getImageUrl() != null && !updateStoreSubscriptionDTO.getImageUrl().isEmpty()) {
                storeSubscription.setImageUrl(updateStoreSubscriptionDTO.getImageUrl());
            }
            if (updateStoreSubscriptionDTO.getPlanType() != null && !updateStoreSubscriptionDTO.getPlanType().isEmpty()) {
                storeSubscription.setPlanType(updateStoreSubscriptionDTO.getPlanType());
            }
            storeSubscriptionRepository.save(storeSubscription);
            return convertToDto(storeSubscription);
        } else {
            throw new ResourceNotFoundException("Store Subscription with ID " + updateStoreSubscriptionDTO.getId() + " not found");
        }
    }

    @Override
    public void deleteStoreSubscriptionById(Long id) {
        if (!storeSubscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Store Subscription with ID " + id + " not found");
        }
        storeSubscriptionRepository.deleteById(id);
    }

    private ResponseStoreSubscription convertToDto(StoreSubscription storeSubscription) {
        return new ResponseStoreSubscription(
                storeSubscription.getId(),
                storeSubscription.getName(),
                storeSubscription.getPrice(),
                storeSubscription.getDescription(),
                storeSubscription.getImageUrl(),
                storeSubscription.getPlanType(),
                storeSubscription.getPurchases()
        );
    }
}
