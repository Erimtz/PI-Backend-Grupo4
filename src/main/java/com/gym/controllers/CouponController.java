package com.gym.controllers;

import com.gym.dto.*;
import com.gym.entities.Coupon;
import com.gym.services.CouponService;
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
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ResponseCouponDTO>> getAllCoupons() {
        List<ResponseCouponDTO> couponDTOS = couponService.getAllCoupons();
        return ResponseEntity.ok(couponDTOS);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseCouponDTO> getCouponById(@PathVariable Long id) {
        ResponseCouponDTO couponDTO = couponService.getCouponById(id);
        return ResponseEntity.ok(couponDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ResponseCouponDTO> createCoupon(@Valid @RequestBody CreateCouponDTO createCouponDTO) {
        ResponseCouponDTO couponDTO = couponService.createCoupon(createCouponDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(couponDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseCouponDTO> updateCoupon(@PathVariable Long id, @Valid @RequestBody UpdateCouponDTO updateCouponDTO) {
        updateCouponDTO.setId(id);
        ResponseCouponDTO couponDTO = couponService.updateCoupon(updateCouponDTO);
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
    public ResponseEntity<List<ResponseCouponDTO>> getAllSpentCoupons() {
        List<ResponseCouponDTO> couponDTOS = couponService.getBySpentTrue();
        return ResponseEntity.ok(couponDTOS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all-not-spent")
    public ResponseEntity<List<ResponseCouponDTO>> getAllNotSpentCoupons() {
        List<ResponseCouponDTO> couponDTOS = couponService.getBySpentFalse();
        return ResponseEntity.ok(couponDTOS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all-expired")
    public ResponseEntity<List<ResponseCouponDTO>> getAllExpiredCoupons() {
        List<ResponseCouponDTO> couponDTOS = couponService.getExpiredCoupons();
        return ResponseEntity.ok(couponDTOS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all-current")
    public ResponseEntity<List<ResponseCouponDTO>> getAllCurrentCoupons() {
        List<ResponseCouponDTO> couponDTOS = couponService.getCurrentCoupons();
        return ResponseEntity.ok(couponDTOS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/convert-response-to-entity")
    public Coupon convertResponseToEntity(@RequestBody ResponseCouponDTO responseCouponDTO) {
        return couponService.convertResponseToEntity(responseCouponDTO);
    }
}
