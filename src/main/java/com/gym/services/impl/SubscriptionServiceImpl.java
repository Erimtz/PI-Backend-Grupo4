package com.gym.services.impl;

import com.gym.dto.SubscriptionDTO;
import com.gym.dto.request.PurchaseRequestDTO;
import com.gym.dto.response.SubscriptionResponseDTO;
import com.gym.entities.Account;
import com.gym.entities.Purchase;
import com.gym.entities.StoreSubscription;
import com.gym.entities.Subscription;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.exceptions.UnauthorizedException;
import com.gym.repositories.StoreSubscriptionRepository;
import com.gym.repositories.SubscriptionRepository;
import com.gym.security.configuration.utils.AccountTokenUtils;
import com.gym.services.PurchaseService;
import com.gym.services.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private StoreSubscriptionRepository storeSubscriptionRepository;
    @Lazy
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    @Lazy
    private AccountService accountService;
    @Autowired
    private final AccountTokenUtils accountTokenUtils;

    public List<SubscriptionResponseDTO> getAllSubscriptions() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        if (subscriptions.isEmpty()) {
            throw new NoSuchElementException("No subscriptions available");
        }
        return subscriptions.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionResponseDTO> getAllExpiredSubscriptions() {
        List<Subscription> subscriptions = subscriptionRepository.findExpiredSubscriptions();
        if (subscriptions.isEmpty()) {
            throw new NoSuchElementException("No subscriptions available");
        }
        return subscriptions.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionResponseDTO> getAllActiveSubscriptions() {
        List<Subscription> subscriptions = subscriptionRepository.findActiveSubscriptions();
        if (subscriptions.isEmpty()) {
            throw new NoSuchElementException("No subscriptions available");
        }
        return subscriptions.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public SubscriptionResponseDTO getSubscriptionById(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription with ID: " + id + " not found"));
        return toResponseDTO(subscription);
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
    public Subscription updateAutomaticRenewal(Long accountId, boolean automaticRenewal, HttpServletRequest request) {
        if (!accountTokenUtils.hasAccessToAccount(request, accountId)) {
            throw new UnauthorizedException("No tiene permiso para modificar esta suscripción.");
        }
        Subscription subscription = subscriptionRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("No se pudo obtener la suscripcion con ID de cuenta: " + accountId));
        subscription.setAutomaticRenewal(automaticRenewal);
        return subscriptionRepository.save(subscription);
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
    public void deleteSubscriptionById(Long id) {
        Optional<Subscription> subscriptionOptional = subscriptionRepository.findById(id);
        if (subscriptionOptional.isPresent()) {
            subscriptionRepository.deleteById(id);
        }
        throw new NoSuchElementException("Subscription with ID " + id + " not found");
    }

    @Override
    public List<SubscriptionResponseDTO> renewExpiredSubscriptions(String token) {
        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions();
        List<Subscription> autoRenewalSubscriptions = expiredSubscriptions.stream()
                .filter(Subscription::getAutomaticRenewal)
                .collect(Collectors.toList());

        for (Subscription subscription : autoRenewalSubscriptions) {
            renewSubscription(subscription, token);
        }
        return autoRenewalSubscriptions.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void renewSubscription(Subscription subscription, String token) {

        StoreSubscription storeSubscription = storeSubscriptionRepository.findByName(subscription.getName());
        if (subscription.getPrice() > 0) {
            PurchaseRequestDTO purchaseRequestDTO = new PurchaseRequestDTO();
            purchaseRequestDTO.setAccountId(subscription.getAccount().getId());
            purchaseRequestDTO.setStoreSubscriptionId(storeSubscription.getId());
            purchaseService.createPurchase(purchaseRequestDTO, token);
        }
    }


    public SubscriptionResponseDTO toResponseDTO(Subscription subscription) {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setSubscriptionId(subscription.getId());
        dto.setAccountId(subscription.getAccount().getId());
        dto.setDocument(subscription.getAccount().getDocument());
        dto.setName(subscription.getName());
        dto.setPrice(subscription.getPrice());
        dto.setImageUrl(subscription.getImageUrl());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setIsExpired(isSubscriptionExpired(subscription));
        dto.setPlanType(subscription.getPlanType());
        dto.setAutomaticRenewal(subscription.getAutomaticRenewal());
        return dto;
    }

    private boolean isSubscriptionExpired(Subscription subscription) {
        LocalDate currentDate = LocalDate.now();
        return subscription.getEndDate().isBefore(currentDate);
    }
}
