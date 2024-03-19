package com.gym.controllers;

import com.gym.dto.SubscriptionDTO;
import com.gym.entities.Subscription;
import com.gym.security.entities.UserEntity;
import com.gym.services.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/subscription")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

   @Operation(summary = "Obtener todas las subcripciones")
    @GetMapping("/get-all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscripciones obtenidas con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            })
    })
    public ResponseEntity<?> getAllSubscriptions(){
        List<SubscriptionDTO> subscriptionList = subscriptionService.getAllSubscriptions()
                .stream()
                .map(subscription -> SubscriptionDTO.builder()
                        .id(subscription.getId())
                        .name(subscription.getName())
                        .price(subscription.getPrice())
                        .imageUrl(subscription.getImageUrl())
                        .startDate(subscription.getStartDate())
                        .endDate(subscription.getEndDate())
                        .planType(subscription.getPlanType())
                        .automaticRenewal(subscription.getAutomaticRenewal())
                        .account(subscription.getAccount())
                        .build()
                ).toList();
        return ResponseEntity.ok(subscriptionList);
    }

   @Operation(summary = "Obtener subscripciones expiradas")
    @GetMapping("/get-expired")
   @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Subscripciones obtenidas con exito", content = {
                   @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
           })
   })
    public ResponseEntity<?> getAllExpiredSubscriptions(){
        List<SubscriptionDTO> expiredSubscriptions = subscriptionService.getAllExpiredSubscriptions()
                .stream()
                .map(subscription -> SubscriptionDTO.builder()
                        .id(subscription.getId())
                        .name(subscription.getName())
                        .price(subscription.getPrice())
                        .imageUrl(subscription.getImageUrl())
                        .startDate(subscription.getStartDate())
                        .endDate(subscription.getEndDate())
                        .planType(subscription.getPlanType())
                        .automaticRenewal(subscription.getAutomaticRenewal())
                        .account(subscription.getAccount())
                        .build()
                ).toList();
        return ResponseEntity.ok(expiredSubscriptions);
    }

   @Operation(summary = "Obtener subscripciones activas")
    @GetMapping("/get-active")
   @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Subscripciones obtenidas con exito", content = {
                   @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
           })
   })
    public ResponseEntity<?> getAllActiveSubscriptions(){
        List<SubscriptionDTO> activeSubscriptions = subscriptionService.getAllActiveSubscriptions()
                .stream()
                .map(subscription -> SubscriptionDTO.builder()
                        .id(subscription.getId())
                        .name(subscription.getName())
                        .price(subscription.getPrice())
                        .imageUrl(subscription.getImageUrl())
                        .startDate(subscription.getStartDate())
                        .endDate(subscription.getEndDate())
                        .planType(subscription.getPlanType())
                        .automaticRenewal(subscription.getAutomaticRenewal())
                        .account(subscription.getAccount())
                        .build()
                ).toList();
        return ResponseEntity.ok(activeSubscriptions);
    }

   @Operation(summary = "Obtener subscripciones por id")
   @GetMapping("/get/{id}")
   @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Subscripcion obtenida con exito", content = {
                   @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
           })
   })
    public ResponseEntity<?> getSubscriptionById(@PathVariable Long id){
        Optional<Subscription> subscriptionOptional = subscriptionService.getSubscriptionById(id);
        if(subscriptionOptional.isPresent()){
            Subscription subscription = subscriptionOptional.get();
            SubscriptionDTO subscriptionDTO = SubscriptionDTO.builder()
                    .id(subscription.getId())
                    .name(subscription.getName())
                    .price(subscription.getPrice())
                    .imageUrl(subscription.getImageUrl())
                    .startDate(subscription.getStartDate())
                    .endDate(subscription.getEndDate())
                    .planType(subscription.getPlanType())
                    .automaticRenewal(subscription.getAutomaticRenewal())
                    .account(subscription.getAccount())
                    .build();
            return ResponseEntity.ok(subscriptionDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear subscripcion")
    @PostMapping("/create") // no se va a usar, se va a crear cuando se cree un usuario
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscripciones creada con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            })
    })
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

    @Operation(summary = "Actualizar subscripcion")
    @PutMapping("/update/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscripciones actualizada con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "404", description = "subscripcion no encontrada",content =
            @Content)

    })
    public ResponseEntity<?> updateSubscription(@PathVariable Long id, @RequestBody SubscriptionDTO subscriptionDTO){
        subscriptionDTO.setId(id); // Establecer el ID en el DTO
        Subscription updatedSubscription = subscriptionService.updateSubscription(subscriptionDTO);
        if (updatedSubscription != null) {
            return ResponseEntity.ok(updatedSubscription);
        } else {
            return ResponseEntity.notFound().build();
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
