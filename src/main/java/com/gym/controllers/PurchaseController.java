package com.gym.controllers;

import com.gym.dto.request.DateRangeDTO;
import com.gym.dto.request.PurchaseRequestDTO;
import com.gym.dto.response.ProductAmountResponseDTO;
import com.gym.dto.response.ProductSalesResponseDTO;
import com.gym.dto.response.PurchaseResponseDTO;
import com.gym.entities.Purchase;
import com.gym.entities.Subscription;
import com.gym.exceptions.CouponDiscountExceededException;
import com.gym.exceptions.InsufficientCreditException;
import com.gym.exceptions.NotEnoughStockException;
import com.gym.security.entities.UserEntity;
import com.gym.entities.Product;
import com.gym.exceptions.*;
import com.gym.security.configuration.utils.AccountTokenUtils;
import com.gym.services.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import java.util.LinkedHashMap;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final AccountTokenUtils accountTokenUtils;

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Crear compra")
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Compra creada con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "500", description = "Error de respuesta",content = @Content),
            @ApiResponse(responseCode = "400", description = "Error de parametros",content = @Content),

    })
    public ResponseEntity<?> createPurchase(@RequestBody PurchaseRequestDTO requestDTO, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            PurchaseResponseDTO purchaseResponseDTO = purchaseService.createPurchase(requestDTO, token);
            return new ResponseEntity<>(purchaseResponseDTO, HttpStatus.CREATED);
        } catch (NotEnoughStockException ex) {
            return new ResponseEntity<>("Not enough stock available", HttpStatus.BAD_REQUEST);
        } catch (InsufficientCreditException ex) {
            return new ResponseEntity<>("Insufficient credit balance", HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException ex) {
            return new ResponseEntity<>("Coupon has already been spent", HttpStatus.BAD_REQUEST);
        } catch (CouponDiscountExceededException ex) {
            return new ResponseEntity<>("Total coupon discount exceeds the maximum allowed", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener ventas por categoría") // Revisar para 20/3
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ventas obtenidas con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sales/by-category")
    public ResponseEntity<?> getSalesByCategory(@RequestBody DateRangeDTO dateRangeDTO) {
        LocalDate startDate = dateRangeDTO.getStartDate();
        LocalDate endDate = dateRangeDTO.getEndDate();

        List<PurchaseResponseDTO> salesByCategory = purchaseService.calculateSalesByCategory(dateRangeDTO);
        return new ResponseEntity<>(salesByCategory, HttpStatus.OK);
    }

    @Operation(summary = "Obtener compras por cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compras obtenidas con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "403", description = "No tienes autorización para acceder al recurso",content = @Content),
            @ApiResponse(responseCode = "500", description = "Ocurrió un error al procesar la solicitud",content = @Content)
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<PurchaseResponseDTO>> getPurchasesByAccount(@PathVariable Long accountId, HttpServletRequest request) {
        try {
            boolean hasAccess = accountTokenUtils.hasAccessToAccount(request, accountId);
            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            List<PurchaseResponseDTO> purchaseResponseDTOs = purchaseService.getPurchasesByAccount(accountId, request);
            return ResponseEntity.ok(purchaseResponseDTOs);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener todas las compras")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compras obtenidas con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "403", description = "No tienes autorización para acceder al recurso",content = @Content),
            @ApiResponse(responseCode = "500", description = "Ocurrió un error al procesar la solicitud",content = @Content)
    })
    @GetMapping("/get-all")
    public ResponseEntity<List<PurchaseResponseDTO>> getAllPurchases(HttpServletRequest request) {
        try {
            List<PurchaseResponseDTO> purchaseResponseDTOs = purchaseService.getAllPurchases(request);
            return ResponseEntity.ok(purchaseResponseDTOs);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener compra por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra obtenida con éxito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "500", description = "Ocurrió un error al procesar la solicitud",content = @Content)
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<PurchaseResponseDTO> getPurchaseById(@PathVariable Long id, HttpServletRequest request) {
        try {
            PurchaseResponseDTO purchaseResponseDTO = purchaseService.getPurchaseById(id, request);
            return ResponseEntity.ok(purchaseResponseDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", e);
        }
    }

    @Operation(summary = "Obtener compras por rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compras obtenidas con éxito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "500", description = "Ocurrió un error al procesar la solicitud",content = @Content)
    })
    @GetMapping("/getByDateRange")
    public ResponseEntity<List<PurchaseResponseDTO>> getPurchasesByDateRange(@Valid @RequestBody DateRangeDTO dateRangeDTO) {
        try {
            List<PurchaseResponseDTO> purchases = purchaseService.getAllPurchasesByDateRange(dateRangeDTO);
            return ResponseEntity.ok(purchases);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener total de compras por rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total de compras obtenido con éxito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "500", description = "Ocurrió un error al procesar la solicitud",content = @Content)
    })
    @GetMapping("/getTotalByDateRange")
    public ResponseEntity<Double> getTotalAfterDiscountsSumByDateRange(@Valid @RequestBody DateRangeDTO dateRangeDTO) {
        try {
            Double totalSum = purchaseService.getTotalAfterDiscountsSumByDateRange(dateRangeDTO);
            return ResponseEntity.ok(totalSum);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener cantidad de compras por rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad de compras obtenidas con éxito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "500", description = "Ocurrió un error al procesar la solicitud",content = @Content)
    })
    @GetMapping("/getCountByDateRange")
    public ResponseEntity<Long> getPurchasesCountByDateRange(@Valid @RequestBody DateRangeDTO dateRangeDTO) {
        try {
            Long count = purchaseService.getPurchasesCountByDateRange(dateRangeDTO);
            return ResponseEntity.ok(count);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener promedio de compras por rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promedio obtenido con éxito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "500", description = "Ocurrió un error al procesar la solicitud",content = @Content)
    })
    @GetMapping("/getAverageByDateRange")
    public ResponseEntity<Double> getPurchasesAverageByDateRange(@Valid @RequestBody DateRangeDTO dateRangeDTO) {
        try {
            Double average = purchaseService.getPurchasesAverageByDateRange(dateRangeDTO);
            return ResponseEntity.ok(average);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Promedio de gasto por compra de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra obtenida con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "404", description = "Compras no encontradas",content = @Content),
            @ApiResponse(responseCode = "500", description = "Ocurrió un error al procesar la solicitud",content = @Content)
    })
    @GetMapping("/average-purchase-amount-per-user")
    public ResponseEntity<Double> calculateAveragePurchaseAmountPerUser() {
        try {
            Double averagePurchaseAmountPerUser = purchaseService.calculateAveragePurchaseAmountPerUser();
            return ResponseEntity.ok(averagePurchaseAmountPerUser);
        } catch (NoAccountsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Unidades vendidas por producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra obtenida con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "404", description = "Compras no encontradas",content = @Content),
            @ApiResponse(responseCode = "500", description = "Ocurrió un error al procesar la solicitud",content = @Content)
    })
    @GetMapping("/units-sold-by-product")
    public ResponseEntity<List<ProductSalesResponseDTO>> getProductsByUnitsSold() {
        try {
            List<ProductSalesResponseDTO> products = purchaseService.getUnitsSoldByProduct();
            if (products.isEmpty()) {
                throw new NoDataFoundException("No products found");
            }
            return ResponseEntity.ok(products);
        } catch (NoDataFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Total de ventas por producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra obtenida con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "404", description = "Compras no encontradas",content = @Content),
            @ApiResponse(responseCode = "500", description = "Ocurrió un error al procesar la solicitud",content = @Content)
    })
    @GetMapping("/total-sales-by-product")
    public ResponseEntity<List<ProductAmountResponseDTO>> getSalesByProduct() {
        try {
            List<ProductAmountResponseDTO> salesByProduct = purchaseService.getSalesByProduct();
            if (salesByProduct.isEmpty()) {
                throw new NoDataFoundException("No sales found");
            }
            return ResponseEntity.ok(salesByProduct);
        } catch (NoDataFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}