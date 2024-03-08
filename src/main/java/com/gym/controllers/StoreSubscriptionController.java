package com.gym.controllers;

import com.gym.dto.request.StoreSubscriptionCreateDTO;
import com.gym.dto.response.StoreSubscriptionResponseDTO;
import com.gym.dto.request.StoreSubscriptionUpdateDTO;
import com.gym.services.StoreSubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/store-subscription")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class StoreSubscriptionController {

    private final StoreSubscriptionService storeSubscriptionService;

    @Autowired
    public StoreSubscriptionController(StoreSubscriptionService storeSubscriptionService) {
        this.storeSubscriptionService = storeSubscriptionService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<StoreSubscriptionResponseDTO>> getAllStoreSubscriptions() {
        List<StoreSubscriptionResponseDTO> subscriptions = storeSubscriptionService.getAllStoreSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<StoreSubscriptionResponseDTO> getStoreSubscriptionById(@PathVariable Long id) {
        StoreSubscriptionResponseDTO subscription = storeSubscriptionService.getStoreSubscriptionById(id);
        return ResponseEntity.ok(subscription);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<StoreSubscriptionResponseDTO> createStoreSubscription(@Valid @RequestBody StoreSubscriptionCreateDTO storeSubscriptionCreateDTO) {
        StoreSubscriptionResponseDTO subscription = storeSubscriptionService.createStoreSubscription(storeSubscriptionCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<StoreSubscriptionResponseDTO> updateStoreSubscription(@PathVariable Long id, @Valid @RequestBody StoreSubscriptionUpdateDTO storeSubscriptionUpdateDTO) {
        storeSubscriptionUpdateDTO.setId(id);
        StoreSubscriptionResponseDTO subscription = storeSubscriptionService.updateStoreSubscription(storeSubscriptionUpdateDTO);
        return ResponseEntity.ok(subscription);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteStoreSubscriptionById(@PathVariable Long id) {
        storeSubscriptionService.deleteStoreSubscriptionById(id);
        return ResponseEntity.noContent().build();
    }
}
