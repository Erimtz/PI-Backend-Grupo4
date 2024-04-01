package com.gym.controllers;

import com.gym.dto.SubscriptionDTO;
import com.gym.dto.response.SubscriptionResponseDTO;
import com.gym.entities.Subscription;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.exceptions.UnauthorizedException;
import com.gym.security.configuration.utils.AccountTokenUtils;
import com.gym.services.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/subscription")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class SubscriptionController {


    private final SubscriptionService subscriptionService;
    private final AccountTokenUtils accountTokenUtils;

    @Operation(summary = "Get all subscriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Subscription.class))
            }),
            @ApiResponse(responseCode = "404", description = "No subscriptions found", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllSubscriptions() {
        try {
            List<SubscriptionResponseDTO> subscriptionList = subscriptionService.getAllSubscriptions();
            return ResponseEntity.ok(subscriptionList);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No subscriptions available");
        }
    }

    @Operation(summary = "Get expired subscriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expired subscriptions retrieved successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Subscription.class))
            }),
            @ApiResponse(responseCode = "404", description = "No expired subscriptions found", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-expired")
    public ResponseEntity<?> getAllExpiredSubscriptions(){
        try {
            List<SubscriptionResponseDTO> subscriptionList = subscriptionService.getAllExpiredSubscriptions();
            return ResponseEntity.ok(subscriptionList);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No subscriptions available");
        }
    }

    @Operation(summary = "Get active subscriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active subscriptions retrieved successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Subscription.class))
            }),
            @ApiResponse(responseCode = "404", description = "No active subscriptions found", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-active")
    public ResponseEntity<?> getAllActiveSubscriptions(){
        try {
            List<SubscriptionResponseDTO> subscriptionList = subscriptionService.getAllActiveSubscriptions();
            return ResponseEntity.ok(subscriptionList);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No subscriptions available");
        }
    }

    @Operation(summary = "Get subscription by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription retrieved successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Subscription.class))
            }),
            @ApiResponse(responseCode = "404", description = "Subscription not found", content = @Content)
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getSubscriptionById(@PathVariable Long id){
        try {
            SubscriptionResponseDTO subscriptionDTO = subscriptionService.getSubscriptionById(id);
            return ResponseEntity.ok(subscriptionDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subscription with ID: " + id + " not found");
        }
    }

    @Operation(summary = "Create subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscription created successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Subscription.class))
            }),
            @ApiResponse(responseCode = "404", description = "Your request could not be processed", content = @Content)
    })
    @PostMapping("/create") // not going to be used, it will be created when a user is created
    public ResponseEntity<?> createSubscription(@RequestBody SubscriptionDTO subscriptionDTO) throws URISyntaxException {
        if (subscriptionDTO.getName().isBlank() || subscriptionDTO.getPrice() == null || subscriptionDTO.getPrice() <= 0 || subscriptionDTO.getAccount() == null || subscriptionDTO.getPlanType() == null){
            return ResponseEntity.badRequest().build();
        }
        Subscription subscription =  Subscription.builder()
                .account(subscriptionDTO.getAccount())
                .name(subscriptionDTO.getName())
                .price(subscriptionDTO.getPrice())
                .imageUrl(subscriptionDTO.getImageUrl())
                .startDate(subscriptionDTO.getStartDate())
                .endDate(subscriptionDTO.getEndDate())
                .planType(subscriptionDTO.getPlanType())
                .automaticRenewal(subscriptionDTO.getAutomaticRenewal())
                .account(subscriptionDTO.getAccount())
                .build();
        return ResponseEntity.created(new URI("subscription/create")).build();
    }

    @Operation(summary = "Update subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription updated successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Subscription.class))}),
            @ApiResponse(responseCode = "404", description = "Subscription not found",content = @Content)
    })
    @PutMapping("/update/{id}") // not used either
    public ResponseEntity<?> updateSubscription(@PathVariable Long id, @RequestBody SubscriptionDTO subscriptionDTO){
        subscriptionDTO.setId(id);
        Subscription updatedSubscription = subscriptionService.updateSubscription(subscriptionDTO);
        if (updatedSubscription != null) {
            return ResponseEntity.ok(updatedSubscription);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get all active subscriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Subscription.class))}),
            @ApiResponse(responseCode = "400", description = "An error occurred while processing the request",content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active-subscription")
    public ResponseEntity<?> getActiveSubscriptionRatio() {
        try {
            double ratio = subscriptionService.calculateActiveSubscriptionRatio();
            return ResponseEntity.ok(ratio);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An error occurred while processing the request");
        }
    }

    @Operation(summary = "Activate automatic subscription renewal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription updated successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Subscription.class))}),
            @ApiResponse(responseCode = "400", description = "Request with incorrect parameters",content = @Content),
            @ApiResponse(responseCode = "401", description = "You are not authorized to access the resource",content = @Content),
            @ApiResponse(responseCode = "404", description = "Subscription not found",content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request",content = @Content)
    })
    @PutMapping("/account/{accountId}/updateAutomaticRenewal")
    public ResponseEntity<String> updateSubscriptionAutomaticRenewal(@PathVariable Long accountId, @RequestParam boolean automaticRenewal, HttpServletRequest request) {
        try {
            if (!accountTokenUtils.hasAccessToAccount(request, accountId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("You do not have permission to modify this subscription.");
            }
            subscriptionService.updateAutomaticRenewal(accountId, automaticRenewal, request);
            return ResponseEntity.ok("The subscription has been successfully updated.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The subscription associated with the provided account was not found.");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("You do not have permission to perform this action.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request.");
        }
    }

    @Operation(summary = "Renew all expired subscriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions renewed successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Subscription.class))}),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request",content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/renew-expired")
    public ResponseEntity<?> renewExpiredSubscriptions(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            List<SubscriptionResponseDTO> renewedSubscriptions = subscriptionService.renewExpiredSubscriptions(token);
            return ResponseEntity.status(HttpStatus.OK).body(renewedSubscriptions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }

    @Operation(summary = "Delete a subscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subscription deleted successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Subscription.class))}),
            @ApiResponse(responseCode = "400", description = "An error occurred while processing the request",content = @Content)
    })
    @DeleteMapping("/delete/{id}") // This endpoint won't be used; it will be deleted when a user is deleted
    public ResponseEntity<?> deleteSubscription(@PathVariable Long id){
        if (id != null){
            subscriptionService.deleteSubscriptionById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }
}

