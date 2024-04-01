package com.gym.controllers;

import com.gym.dto.request.StoreSubscriptionCreateDTO;
import com.gym.dto.response.StoreSubscriptionResponseDTO;
import com.gym.dto.request.StoreSubscriptionUpdateDTO;
import com.gym.entities.StoreSubscription;
import com.gym.services.StoreSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/store-subscription")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class StoreSubscriptionController {

    private final StoreSubscriptionService storeSubscriptionService;

    @Operation(summary = "Get all subscriptions")
    @GetMapping("/get-all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StoreSubscription.class))
            })
    })
    public ResponseEntity<List<StoreSubscriptionResponseDTO>> getAllStoreSubscriptions() {
        List<StoreSubscriptionResponseDTO> subscriptions = storeSubscriptionService.getAllStoreSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @Operation(summary = "Get subscription by ID")
    @GetMapping("/get/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StoreSubscription.class))
            })
    })
    public ResponseEntity<StoreSubscriptionResponseDTO> getStoreSubscriptionById(@PathVariable Long id) {
        StoreSubscriptionResponseDTO subscription = storeSubscriptionService.getStoreSubscriptionById(id);
        return ResponseEntity.ok(subscription);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create subscription")
    @PostMapping("/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription created successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StoreSubscription.class))
            })
    })
    public ResponseEntity<StoreSubscriptionResponseDTO> createStoreSubscription(@Valid @RequestBody StoreSubscriptionCreateDTO storeSubscriptionCreateDTO) {
        StoreSubscriptionResponseDTO subscription = storeSubscriptionService.createStoreSubscription(storeSubscriptionCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update subscription")
    @PutMapping("/update/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription updated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StoreSubscription.class))
            })
    })
    public ResponseEntity<StoreSubscriptionResponseDTO> updateStoreSubscription(@PathVariable Long id, @Valid @RequestBody StoreSubscriptionUpdateDTO storeSubscriptionUpdateDTO) {
        storeSubscriptionUpdateDTO.setId(id);
        StoreSubscriptionResponseDTO subscription = storeSubscriptionService.updateStoreSubscription(storeSubscriptionUpdateDTO);
        return ResponseEntity.ok(subscription);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete subscription by ID")
    @DeleteMapping("/delete/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subscription deleted successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = StoreSubscription.class))
            })
    })
    public ResponseEntity<Void> deleteStoreSubscriptionById(@PathVariable Long id) {
        storeSubscriptionService.deleteStoreSubscriptionById(id);
        return ResponseEntity.noContent().build();
    }
}
