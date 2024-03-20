package com.gym.controllers;

import com.gym.dto.SubscriptionDTO;
import com.gym.dto.response.SubscriptionResponseDTO;
import com.gym.entities.Subscription;
import com.gym.security.entities.UserEntity;
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

   @Operation(summary = "Obtener todas las subcripciones")
    @GetMapping("/get-all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscripciones obtenidas con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            })
    })
    public ResponseEntity<?> getAllSubscriptions() {
        try {
            List<SubscriptionResponseDTO> subscriptionList = subscriptionService.getAllSubscriptions();
            return ResponseEntity.ok(subscriptionList);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No subscriptions available");
        }
    }

   @Operation(summary = "Obtener subscripciones expiradas")
    @GetMapping("/get-expired")
   @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Subscripciones obtenidas con exito", content = {
                   @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
           }),
           @ApiResponse(responseCode = "404", description = "No se encontraron suscripciones", content = @Content)
    public ResponseEntity<?> getAllExpiredSubscriptions(){
        try {
            List<SubscriptionResponseDTO> subscriptionList = subscriptionService.getAllExpiredSubscriptions();
            return ResponseEntity.ok(subscriptionList);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No subscriptions available");
        }
    }

   @Operation(summary = "Obtener subscripciones activas")
    @GetMapping("/get-active")
   @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Subscripciones obtenidas con exito", content = {
                   @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
           }),
           @ApiResponse(responseCode = "404", description = "No se encontraron suscripciones", content = @Content)
   })
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
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subscription with ID: " + id + " not found");
        }
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscripciones actualizada con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            }),
            @ApiResponse(responseCode = "404", description = "subscripcion no encontrada",content =
            @Content)

    })
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

    @GetMapping("/active-subscription")
    public ResponseEntity<Double> getActiveSubscriptionRatio() {
        double ratio = subscriptionService.calculateActiveSubscriptionRatio();
        return ResponseEntity.ok(ratio);
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

    @GetMapping("/renew-expired")
    public ResponseEntity<List<SubscriptionResponseDTO>> renewExpiredSubscriptions(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        List<SubscriptionResponseDTO> renewedSubscriptions = subscriptionService.renewExpiredSubscriptions(token);
        return ResponseEntity.status(HttpStatus.OK).body(renewedSubscriptions);
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
