package com.gym.controllers;

import com.gym.dto.request.StoreSubscriptionCreateDTO;
import com.gym.dto.response.StoreSubscriptionResponseDTO;
import com.gym.dto.request.StoreSubscriptionUpdateDTO;
import com.gym.security.entities.UserEntity;
import com.gym.services.StoreSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Traer todas las subscripciones")
    @GetMapping("/get-all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscripciones obtenidas con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            })
    })
    public ResponseEntity<List<StoreSubscriptionResponseDTO>> getAllStoreSubscriptions() {
        List<StoreSubscriptionResponseDTO> subscriptions = storeSubscriptionService.getAllStoreSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @Operation(summary = "Traer subscripcion por ID")
    @GetMapping("/get/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscripcion obtenida con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            })
    })
    public ResponseEntity<StoreSubscriptionResponseDTO> getStoreSubscriptionById(@PathVariable Long id) {
        StoreSubscriptionResponseDTO subscription = storeSubscriptionService.getStoreSubscriptionById(id);
        return ResponseEntity.ok(subscription);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear subscripcion")
    @PostMapping("/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Subscripcion creada con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            })
    })
    public ResponseEntity<StoreSubscriptionResponseDTO> createStoreSubscription(@Valid @RequestBody StoreSubscriptionCreateDTO storeSubscriptionCreateDTO) {
        StoreSubscriptionResponseDTO subscription = storeSubscriptionService.createStoreSubscription(storeSubscriptionCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar subscripcion")
    @PutMapping("/update/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscripcion actualizada con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            })
    })
    public ResponseEntity<StoreSubscriptionResponseDTO> updateStoreSubscription(@PathVariable Long id, @Valid @RequestBody StoreSubscriptionUpdateDTO storeSubscriptionUpdateDTO) {
        storeSubscriptionUpdateDTO.setId(id);
        StoreSubscriptionResponseDTO subscription = storeSubscriptionService.updateStoreSubscription(storeSubscriptionUpdateDTO);
        return ResponseEntity.ok(subscription);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar subscripcion por ID")
    @DeleteMapping("/delete/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Subscripcion eliminada con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = UserEntity.class))
            })
    })
    public ResponseEntity<Void> deleteStoreSubscriptionById(@PathVariable Long id) {
        storeSubscriptionService.deleteStoreSubscriptionById(id);
        return ResponseEntity.noContent().build();
    }
}
