package com.gym.controllers;

import com.gym.dto.CreateStoreSubscriptionDTO;
import com.gym.dto.ResponseStoreSubscription;
import com.gym.dto.UpdateStoreSubscriptionDTO;
import com.gym.services.StoreSubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/store-subscription")
public class StoreSubscriptionController {

    private final StoreSubscriptionService storeSubscriptionService;

    @Autowired
    public StoreSubscriptionController(StoreSubscriptionService storeSubscriptionService) {
        this.storeSubscriptionService = storeSubscriptionService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ResponseStoreSubscription>> getAllStoreSubscriptions() {
        List<ResponseStoreSubscription> subscriptions = storeSubscriptionService.getAllStoreSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseStoreSubscription> getStoreSubscriptionById(@PathVariable Long id) {
        ResponseStoreSubscription subscription = storeSubscriptionService.getStoreSubscriptionById(id);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseStoreSubscription> createStoreSubscription(@Valid @RequestBody CreateStoreSubscriptionDTO createStoreSubscriptionDTO) {
        ResponseStoreSubscription subscription = storeSubscriptionService.createStoreSubscription(createStoreSubscriptionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseStoreSubscription> updateStoreSubscription(@PathVariable Long id, @Valid @RequestBody UpdateStoreSubscriptionDTO updateStoreSubscriptionDTO) {
        updateStoreSubscriptionDTO.setId(id);
        ResponseStoreSubscription subscription = storeSubscriptionService.updateStoreSubscription(updateStoreSubscriptionDTO);
        return ResponseEntity.ok(subscription);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteStoreSubscriptionById(@PathVariable Long id) {
        storeSubscriptionService.deleteStoreSubscriptionById(id);
        return ResponseEntity.noContent().build();
    }
}
