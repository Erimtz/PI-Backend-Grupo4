package com.gym.controllers;

import com.gym.dto.request.DateRangeDTO;
import com.gym.dto.request.PurchaseRequestDTO;
import com.gym.dto.response.*;
import com.gym.entities.Purchase;
import com.gym.exceptions.CouponDiscountExceededException;
import com.gym.exceptions.InsufficientCreditException;
import com.gym.exceptions.NotEnoughStockException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final AccountTokenUtils accountTokenUtils;

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create purchase")
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Purchase created successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "500", description = "Error response",content = @Content),
            @ApiResponse(responseCode = "400", description = "Parameter error",content = @Content),
    })
    public ResponseEntity<?> createPurchase(@RequestBody PurchaseRequestDTO requestDTO, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            PurchaseResponseDTO purchaseResponseDTO = purchaseService.createPurchase(requestDTO, token);
            return new ResponseEntity<>(purchaseResponseDTO, HttpStatus.CREATED);
        } catch (NotEnoughStockException ex) {
            return new ResponseEntity<>(new Message("Not enough stock available"), HttpStatus.BAD_REQUEST);
        } catch (InsufficientCreditException ex) {
            return new ResponseEntity<>(new Message("Insufficient credit balance"), HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException ex) {
            return new ResponseEntity<>(new Message("Coupon has already been spent"), HttpStatus.BAD_REQUEST);
        } catch (CouponDiscountExceededException ex) {
            return new ResponseEntity<>(new Message("Total coupon discount exceeds the maximum allowed"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Message("Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get sales by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales obtained successfully", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Purchase.class))}),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sales/by-category")
    public ResponseEntity<?> getSalesByCategory(@RequestBody DateRangeDTO dateRangeDTO) {
        LocalDate startDate = dateRangeDTO.getStartDate();
        LocalDate endDate = dateRangeDTO.getEndDate();

        List<CategorySalesResponseDTO> salesByCategory = purchaseService.calculateSalesByCategory(dateRangeDTO);
        return new ResponseEntity<>(salesByCategory, HttpStatus.OK);
    }

    @Operation(summary = "Get purchases by account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchases obtained successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "403", description = "You are not authorized to access the resource", content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request", content = @Content)
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

    @Operation(summary = "Get all purchases")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchases obtained successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "403", description = "You are not authorized to access the resource", content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
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

    @Operation(summary = "Get purchase by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request", content = @Content)
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<PurchaseResponseDTO> getPurchaseById(@PathVariable Long id, HttpServletRequest request) {
        try {
            PurchaseResponseDTO purchaseResponseDTO = purchaseService.getPurchaseById(id, request);
            return ResponseEntity.ok(purchaseResponseDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @Operation(summary = "Get purchases by date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchases retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Purchase.class))}),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getByDateRange")
    public ResponseEntity<List<PurchaseResponseDTO>> getPurchasesByDateRange(@Valid @RequestBody DateRangeDTO dateRangeDTO) {
        try {
            List<PurchaseResponseDTO> purchases = purchaseService.getAllPurchasesByDateRange(dateRangeDTO);
            return ResponseEntity.ok(purchases);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get total purchases by date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total purchases retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class))}),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getTotalByDateRange")
    public ResponseEntity<Double> getTotalAfterDiscountsSumByDateRange(@Valid @RequestBody DateRangeDTO dateRangeDTO) {
        try {
            Double totalSum = purchaseService.getTotalAfterDiscountsSumByDateRange(dateRangeDTO);
            return ResponseEntity.ok(totalSum);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get number of purchases by date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Number of purchases retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))}),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getCountByDateRange")
    public ResponseEntity<Long> getPurchasesCountByDateRange(@Valid @RequestBody DateRangeDTO dateRangeDTO) {
        try {
            Long count = purchaseService.getPurchasesCountByDateRange(dateRangeDTO);
            return ResponseEntity.ok(count);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get average purchases by date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Average retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class))}),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAverageByDateRange")
    public ResponseEntity<Double> getPurchasesAverageByDateRange(@Valid @RequestBody DateRangeDTO dateRangeDTO) {
        try {
            Double average = purchaseService.getPurchasesAverageByDateRange(dateRangeDTO);
            return ResponseEntity.ok(average);
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Average spending per user purchase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class))}),
            @ApiResponse(responseCode = "404", description = "Purchases not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
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

    @Operation(summary = "Units sold per product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductSalesResponseDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Purchases not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
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

    @Operation(summary = "Total sales per product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductAmountResponseDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Sales not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while processing the request", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
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