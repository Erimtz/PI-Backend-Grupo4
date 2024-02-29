package com.gym.services;

import com.gym.dto.PurchaseDTO;
import com.gym.dto.PurchaseDetailDTO;
import com.gym.entities.*;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.PurchaseDetailRepository;
import com.gym.repositories.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService{

    private PurchaseRepository purchaseRepository;

    @Override
    public List<PurchaseDTO> getAllPurchases() {
        return purchaseRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseDTO getPurchaseById(Long id) {
        return purchaseRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase with ID " + id + " not found"));
    }

    @Override
    public PurchaseDTO createPurchase(PurchaseDTO purchaseDTO) {
        try {
            Purchase purchase = Purchase.builder()
                    .storeSubscription(purchaseDTO.getStoreSubscription())
                    .purchaseDetails(purchaseDTO.getPurchaseDetails())
                    .couponsApplied(purchaseDTO.getCouponsApplied())
                    .purchaseDate(purchaseDTO.getPurchaseDate())
                    .account(purchaseDTO.getAccount())
                    .build();
            purchaseRepository.save(purchase);
            return convertToDto(purchase);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while saving purchase details", e);
        }
    }

    @Override
    public PurchaseDTO updatePurchase(PurchaseDTO purchaseDTO) {
        Optional<Purchase> purchaseOptional = purchaseRepository.findById(purchaseDTO.getId());
        if (purchaseOptional.isPresent()){
            Purchase purchase = purchaseOptional.get();

            if (purchaseDTO.getStoreSubscription() != null) {
                purchase.setStoreSubscription(purchaseDTO.getStoreSubscription());
            }
            if (purchaseDTO.getPurchaseDetails() != null) {
                purchase.setPurchaseDetails(purchaseDTO.getPurchaseDetails());
            }
            if (purchaseDTO.getCouponsApplied() != null) {
                purchase.setCouponsApplied(purchaseDTO.getCouponsApplied());
            }
            if (purchaseDTO.getPurchaseDate() != null) {
                purchase.setPurchaseDate(purchaseDTO.getPurchaseDate());
            }
            if (purchaseDTO.getAccount() != null) {
                purchase.setAccount(purchaseDTO.getAccount());
            }
            purchaseRepository.save(purchase);
            return convertToDto(purchase);
        } else {
            throw new ResourceNotFoundException("Purchase with ID " + purchaseDTO.getId() + " not found");
        }
    }

    @Override
    public void deletePurchaseById(Long id) {
        if (!purchaseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Purchase details with ID " + id + " not found");
        }
        purchaseRepository.deleteById(id);
    }

    private PurchaseDTO convertToDto(Purchase purchase) {
        return new PurchaseDTO(
                purchase.getId(),
                purchase.getStoreSubscription(),
                purchase.getPurchaseDetails(),
                purchase.getCouponsApplied(),
                purchase.getPurchaseDate(),
                purchase.getAccount()
        );
    }
}
