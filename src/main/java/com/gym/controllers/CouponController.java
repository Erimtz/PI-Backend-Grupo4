package com.gym.controllers;

import com.gym.dto.*;
import com.gym.dto.request.CouponCreateDTO;
import com.gym.dto.request.CouponUpdateDTO;
import com.gym.entities.Coupon;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.exceptions.UnauthorizedException;
import com.gym.security.configuration.utils.AccountTokenUtils;
import com.gym.services.CouponService;
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

    @GetMapping("/get-all")
    public ResponseEntity<List<CouponResponseDTO>> getAllCoupons() {
        List<CouponResponseDTO> couponDTOS = couponService.getAllCoupons();
        return ResponseEntity.ok(couponDTOS);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<CouponResponseDTO> getCouponById(@PathVariable Long id) {
        CouponResponseDTO couponDTO = couponService.getCouponById(id);
        return ResponseEntity.ok(couponDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<CouponResponseDTO> createCoupon(@Valid @RequestBody CouponCreateDTO couponCreateDTO) {
        CouponResponseDTO couponDTO = couponService.createCoupon(couponCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(couponDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<CouponResponseDTO> updateCoupon(@PathVariable Long id, @Valid @RequestBody CouponUpdateDTO couponUpdateDTO) {
        couponUpdateDTO.setId(id);
        CouponResponseDTO couponDTO = couponService.updateCoupon(couponUpdateDTO);
        return ResponseEntity.ok(couponDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCouponById(@PathVariable Long id) {
        couponService.deleteCouponById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all-spent")
    public ResponseEntity<List<CouponResponseDTO>> getAllSpentCoupons() {
        List<CouponResponseDTO> couponDTOS = couponService.getBySpentTrue();
        return ResponseEntity.ok(couponDTOS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all-not-spent")
    public ResponseEntity<List<CouponResponseDTO>> getAllNotSpentCoupons() {
        List<CouponResponseDTO> couponDTOS = couponService.getBySpentFalse();
        return ResponseEntity.ok(couponDTOS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all-expired")
    public ResponseEntity<List<CouponResponseDTO>> getAllExpiredCoupons() {
        List<CouponResponseDTO> couponDTOS = couponService.getExpiredCoupons();
        return ResponseEntity.ok(couponDTOS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all-current")
    public ResponseEntity<List<CouponResponseDTO>> getAllCurrentCoupons() {
        List<CouponResponseDTO> couponDTOS = couponService.getCurrentCoupons();
        return ResponseEntity.ok(couponDTOS);
    }

    @GetMapping("/valid-coupons/{accountId}")
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

    @GetMapping("/effectiveness")
    public ResponseEntity<Double> calculateCouponEffectiveness() {
        double effectiveness = couponService.calculateCouponEffectiveness();
        return ResponseEntity.ok(effectiveness);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/convert-response-to-entity")
    public Coupon convertResponseToEntity(@RequestBody CouponResponseDTO couponResponseDTO) {
        return couponService.convertResponseToEntity(couponResponseDTO);
    }
}
