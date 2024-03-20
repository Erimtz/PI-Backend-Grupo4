package com.gym.controllers;

import com.gym.dto.*;
import com.gym.dto.request.CouponCreateDTO;
import com.gym.dto.request.CouponUpdateDTO;
import com.gym.entities.Coupon;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.exceptions.UnauthorizedException;
import com.gym.security.configuration.utils.AccountTokenUtils;
import com.gym.security.entities.UserEntity;
import com.gym.services.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;
    private final AccountTokenUtils accountTokenUtils;

    @Autowired
    public CouponController(CouponService couponService, AccountTokenUtils accountTokenUtils) {
        this.couponService = couponService;
        this.accountTokenUtils = accountTokenUtils;
    }

    @Operation(summary = "Traer todos los cupones")
    @GetMapping("/get-all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupones obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Coupon.class))
            })
    })
    public ResponseEntity<List<CouponResponseDTO>> getAllCoupons() {
        List<CouponResponseDTO> couponDTOS = couponService.getAllCoupons();
        return ResponseEntity.ok(couponDTOS);
    }

    @Operation(summary = "Traer cupones por id ")
    @GetMapping("/get/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupones obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Coupon.class))
            })
    })
    public ResponseEntity<CouponResponseDTO> getCouponById(@PathVariable Long id) {
        CouponResponseDTO couponDTO = couponService.getCouponById(id);
        return ResponseEntity.ok(couponDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear cupon")
    @PostMapping("/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cupon creado con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Coupon.class))
            })
    })
    public ResponseEntity<CouponResponseDTO> createCoupon(@Valid @RequestBody CouponCreateDTO couponCreateDTO) {
        CouponResponseDTO couponDTO = couponService.createCoupon(couponCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(couponDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar cupon")
    @PutMapping("/update/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupon actualizado con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Coupon.class))
            })
    })
    public ResponseEntity<CouponResponseDTO> updateCoupon(@PathVariable Long id, @Valid @RequestBody CouponUpdateDTO couponUpdateDTO) {
        couponUpdateDTO.setId(id);
        CouponResponseDTO couponDTO = couponService.updateCoupon(couponUpdateDTO);
        return ResponseEntity.ok(couponDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Elimnar cupon por id")
    @DeleteMapping("/delete/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cupones eliminado con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Coupon.class))
            })
    })
    public ResponseEntity<Void> deleteCouponById(@PathVariable Long id) {
        couponService.deleteCouponById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Traer cupones consumidos")
    @GetMapping("/get-all-spent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupones obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Coupon.class))
            })
    })
    public ResponseEntity<List<CouponResponseDTO>> getAllSpentCoupons() {
        List<CouponResponseDTO> couponDTOS = couponService.getBySpentTrue();
        return ResponseEntity.ok(couponDTOS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Traer cupones no consumidos")
    @GetMapping("/get-all-not-spent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupones obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Coupon.class))
            })
    })
    public ResponseEntity<List<CouponResponseDTO>> getAllNotSpentCoupons() {
        List<CouponResponseDTO> couponDTOS = couponService.getBySpentFalse();
        return ResponseEntity.ok(couponDTOS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Traer cupones expirados")
    @GetMapping("/get-all-expired")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupones obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Coupon.class))
            })
    })
    public ResponseEntity<List<CouponResponseDTO>> getAllExpiredCoupons() {
        List<CouponResponseDTO> couponDTOS = couponService.getExpiredCoupons();
        return ResponseEntity.ok(couponDTOS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Traer cupones vigentes")
    @GetMapping("/get-all-current")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupones obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Coupon.class))
            })
    })
    public ResponseEntity<List<CouponResponseDTO>> getAllCurrentCoupons() {
        List<CouponResponseDTO> couponDTOS = couponService.getCurrentCoupons();
        return ResponseEntity.ok(couponDTOS);
    }

    @Operation(summary = "Traer cupones validos de cuenta")
    @GetMapping("/valid-coupons/{accountId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupones obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Coupon.class))
            }),
            @ApiResponse(responseCode = "403", description = "No está autorizado a acceder al recurso",content =
            @Content),
            @ApiResponse(responseCode = "500", description = "Ocurrió un error al procesar la solicitud",content =
            @Content),
            @ApiResponse(responseCode = "404", description = "Cupón no encontrado",content =
            @Content)
    })
    public ResponseEntity<List<CouponResponseDTO>> getValidCouponsByAccount(@PathVariable Long accountId, HttpServletRequest request) {
        try {
            boolean hasAccess = accountTokenUtils.hasAccessToAccount(request, accountId);
            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            List<CouponResponseDTO> validCoupons = couponService.getValidCouponsByAccount(accountId, request);
            return ResponseEntity.ok(validCoupons);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Porcentaje de efectividad de los cupones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Porcentaje obtenido sin errores", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Coupon.class))})
    })
    @GetMapping("/effectiveness")
    public ResponseEntity<Double> calculateCouponEffectiveness() {
        double effectiveness = couponService.calculateCouponEffectiveness();
        return ResponseEntity.ok(effectiveness);
    }

    @Operation(summary = "Traer cupones por cuenta de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupones obtenidos con exito", content = {
                    @Content(mediaType = "application/json",schema = @Schema(implementation = Coupon.class))
            }),
            @ApiResponse(responseCode = "403", description = "No está autorizado a acceder al recurso",content =
            @Content),
            @ApiResponse(responseCode = "500", description = "Ocurrió un error al procesar la solicitud",content =
            @Content),
            @ApiResponse(responseCode = "404", description = "Cupón no encontrado",content =
            @Content)
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CouponResponseDTO>> getCouponsByAccount(@PathVariable Long accountId, HttpServletRequest request) {
        try {
            boolean hasAccess = accountTokenUtils.hasAccessToAccount(request, accountId);
            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            List<CouponResponseDTO> validCoupons = couponService.getCouponsByAccount(accountId, request);
            return ResponseEntity.ok(validCoupons);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/convert-response-to-entity")
    public Coupon convertResponseToEntity(@RequestBody CouponResponseDTO couponResponseDTO) {
        return couponService.convertResponseToEntity(couponResponseDTO);
    }
}
