package com.gym.services.impl;

import com.gym.dto.request.StoreSubscriptionCreateDTO;
import com.gym.dto.response.StoreSubscriptionResponseDTO;
import com.gym.dto.request.StoreSubscriptionUpdateDTO;
import com.gym.entities.StoreSubscription;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.StoreSubscriptionRepository;
import com.gym.services.StoreSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreSubscriptionServiceImpl implements StoreSubscriptionService {

    private final StoreSubscriptionRepository storeSubscriptionRepository;

    @Override
    public List<StoreSubscriptionResponseDTO> getAllStoreSubscriptions() {
        return storeSubscriptionRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public StoreSubscriptionResponseDTO getStoreSubscriptionById(Long id) {
        return storeSubscriptionRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Store Subscription with ID " + id + " not found"));
    }

    @Override
    public StoreSubscriptionResponseDTO createStoreSubscription(StoreSubscriptionCreateDTO storeSubscriptionCreateDTO) {
        try {
            StoreSubscription storeSubscription = StoreSubscription.builder()
                    .name(storeSubscriptionCreateDTO.getName())
                    .price(storeSubscriptionCreateDTO.getPrice())
                    .description(storeSubscriptionCreateDTO.getDescription())
                    .imageUrl(storeSubscriptionCreateDTO.getImageUrl())
                    .planType(storeSubscriptionCreateDTO.getPlanType())
                    .durationDays(storeSubscriptionCreateDTO.getDurationDays())
                    .purchases(new ArrayList<>())
                    .build();
            storeSubscriptionRepository.save(storeSubscription);
            return convertToDto(storeSubscription);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while saving store subscription", e);
        }
    }

    @Override
    public StoreSubscriptionResponseDTO updateStoreSubscription(StoreSubscriptionUpdateDTO storeSubscriptionUpdateDTO) {
        Optional<StoreSubscription> storeSubscriptionOptional = storeSubscriptionRepository.findById(storeSubscriptionUpdateDTO.getId());
        if (storeSubscriptionOptional.isPresent()){
            StoreSubscription storeSubscription = storeSubscriptionOptional.get();

            if (storeSubscriptionUpdateDTO.getName() != null && !storeSubscriptionUpdateDTO.getName().isEmpty()) {
                storeSubscription.setName(storeSubscriptionUpdateDTO.getName());
            }
            if (storeSubscriptionUpdateDTO.getPrice() != null) {
                storeSubscription.setPrice(storeSubscriptionUpdateDTO.getPrice());
            }
            if (storeSubscriptionUpdateDTO.getDescription() != null){
                storeSubscription.setDescription(storeSubscriptionUpdateDTO.getDescription());
            }
            if (storeSubscriptionUpdateDTO.getImageUrl() != null && !storeSubscriptionUpdateDTO.getImageUrl().isEmpty()) {
                storeSubscription.setImageUrl(storeSubscriptionUpdateDTO.getImageUrl());
            }
            if (storeSubscriptionUpdateDTO.getPlanType() != null && !storeSubscriptionUpdateDTO.getPlanType().isEmpty()) {
                storeSubscription.setPlanType(storeSubscriptionUpdateDTO.getPlanType());
            }
            if (storeSubscriptionUpdateDTO.getDurationDays() != null) {
                storeSubscription.setDurationDays(storeSubscriptionUpdateDTO.getDurationDays());
            }
            storeSubscriptionRepository.save(storeSubscription);
            return convertToDto(storeSubscription);
        } else {
            throw new ResourceNotFoundException("Store Subscription with ID " + storeSubscriptionUpdateDTO.getId() + " not found");
        }
    }

    @Override
    public void deleteStoreSubscriptionById(Long id) {
        if (!storeSubscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Store Subscription with ID " + id + " not found");
        }
        storeSubscriptionRepository.deleteById(id);
    }

    private StoreSubscriptionResponseDTO convertToDto(StoreSubscription storeSubscription) {
        return new StoreSubscriptionResponseDTO(
                storeSubscription.getId(),
                storeSubscription.getName(),
                storeSubscription.getPrice(),
                storeSubscription.getDescription(),
                storeSubscription.getImageUrl(),
                storeSubscription.getPlanType(),
                storeSubscription.getDurationDays(),
                storeSubscription.getPurchases()

        );
    }
    @Override
    public StoreSubscription convertToEntity(StoreSubscriptionResponseDTO storeSubscriptionResponseDTO) {
        StoreSubscription storeSubscription = new StoreSubscription();
        storeSubscription.setId(storeSubscriptionResponseDTO.getId());
        storeSubscription.setName(storeSubscriptionResponseDTO.getName());
        storeSubscription.setPrice(storeSubscriptionResponseDTO.getPrice());
        storeSubscription.setDescription(storeSubscriptionResponseDTO.getDescription());
        storeSubscription.setImageUrl(storeSubscriptionResponseDTO.getImageUrl());
        storeSubscription.setPlanType(storeSubscriptionResponseDTO.getPlanType());
        storeSubscription.setDurationDays(storeSubscriptionResponseDTO.getDurationDays());
        return storeSubscription;
    }
}
