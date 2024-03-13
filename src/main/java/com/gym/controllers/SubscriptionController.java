package com.gym.controllers;

import com.gym.dto.SubscriptionDTO;
import com.gym.dto.response.SubscriptionResponseDTO;
import com.gym.entities.Subscription;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.exceptions.UnauthorizedException;
import com.gym.security.configuration.utils.AccountTokenUtils;
import com.gym.services.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/subscription")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private AccountTokenUtils accountTokenUtils;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllSubscriptions() {
        try {
            List<SubscriptionResponseDTO> subscriptionList = subscriptionService.getAllSubscriptions();
            return ResponseEntity.ok(subscriptionList);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No subscriptions available");
        }
    }

    @GetMapping("/get-expired")
    public ResponseEntity<?> getAllExpiredSubscriptions(){
        try {
            List<SubscriptionResponseDTO> subscriptionList = subscriptionService.getAllExpiredSubscriptions();
            return ResponseEntity.ok(subscriptionList);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No subscriptions available");
        }
    }

    @GetMapping("/get-active")
    public ResponseEntity<?> getAllActiveSubscriptions(){
        try {
            List<SubscriptionResponseDTO> subscriptionList = subscriptionService.getAllActiveSubscriptions();
            return ResponseEntity.ok(subscriptionList);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No subscriptions available");
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getSubscriptionById(@PathVariable Long id) {
        try {
            SubscriptionResponseDTO subscriptionDTO = subscriptionService.getSubscriptionById(id);
            return ResponseEntity.ok(subscriptionDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subscription with ID: " + id + " not found");
        }
    }

    @PostMapping("/create") // no se va a usar, se va a crear cuando se cree un usuario
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

    @PutMapping("/update/{id}") // tampoco se usa
    public ResponseEntity<?> updateSubscription(@PathVariable Long id, @RequestBody SubscriptionDTO subscriptionDTO){
        subscriptionDTO.setId(id);
        Subscription updatedSubscription = subscriptionService.updateSubscription(subscriptionDTO);
        if (updatedSubscription != null) {
            return ResponseEntity.ok(updatedSubscription);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/account/{accountId}/updateAutomaticRenewal")
    public ResponseEntity<String> updateSubscriptionAutomaticRenewal(@PathVariable Long accountId,
                                                                     @RequestParam boolean automaticRenewal,
                                                                     HttpServletRequest request) {
        try {
            if (!accountTokenUtils.hasAccessToAccount(request, accountId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("No tiene permiso para modificar esta suscripción.");
            }
            subscriptionService.updateAutomaticRenewal(accountId, automaticRenewal, request);
            return ResponseEntity.ok("La suscripción ha sido actualizada exitosamente.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la suscripción asociada a la cuenta proporcionada.");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("No tiene permiso para realizar esta acción.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al procesar la solicitud.");
        }
    }

    @DeleteMapping("/delete/{id}") // no se va a usar, se va a borrar cuendo se borre un usuario
    public ResponseEntity<?> deleteSubscription(@PathVariable Long id){
        if (id!=null){
            subscriptionService.deleteSubscriptionById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
