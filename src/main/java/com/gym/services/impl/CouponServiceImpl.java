package com.gym.services.impl;

import com.gym.dto.request.CouponCreateDTO;
import com.gym.dto.CouponResponseDTO;
import com.gym.dto.request.CouponUpdateDTO;
import com.gym.entities.Account;
import com.gym.entities.Coupon;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.exceptions.UnauthorizedException;
import com.gym.repositories.AccountRepository;
import com.gym.repositories.CouponRepository;
//import com.gym.security.configuration.utils.AccessValidationUtils;
import com.gym.security.configuration.utils.AccountTokenUtils;
import com.gym.services.CouponService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);
    private final CouponRepository couponRepository;
    private final AccountRepository accountRepository;
    private final AccountTokenUtils accountTokenUtils;
//    private final AccessValidationUtils accessValidationUtils;

    @Override
    public List<CouponResponseDTO> getAllCoupons() {
        return couponRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponseDTO getCouponById(Long id) {
        return couponRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> {
                    logger.error("No coupon found with ID: {}", id);
                    return new ResourceNotFoundException("Coupon with ID " + id + " not found");
                });
    }

    @Override
    public CouponResponseDTO createCoupon(CouponCreateDTO couponCreateDTO) {
        try {
            Coupon coupon = Coupon.builder()
                    .issueDate(couponCreateDTO.getIssueDate())
                    .dueDate(couponCreateDTO.getDueDate())
                    .amount(couponCreateDTO.getAmount())
                    .spent(couponCreateDTO.getSpent())
                    .account(couponCreateDTO.getAccount())
                    .build();
            couponRepository.save(coupon);
            return convertToDto(coupon);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while saving coupon", e);
        }
    }

    @Override
    public CouponResponseDTO updateCoupon(CouponUpdateDTO couponUpdateDTO) {
        Optional<Coupon> couponOptional = couponRepository.findById(couponUpdateDTO.getId());
        if (couponOptional.isPresent()){
            Coupon coupon = couponOptional.get();

            if (couponUpdateDTO.getIssueDate() != null) {
                coupon.setIssueDate(couponUpdateDTO.getIssueDate());
            }
            if (couponUpdateDTO.getDueDate() != null) {
                coupon.setDueDate(couponUpdateDTO.getDueDate());
            }
            if (couponUpdateDTO.getAmount() != null) {
                coupon.setAmount(couponUpdateDTO.getAmount());
            }
            if (couponUpdateDTO.getSpent() != null) {
                coupon.setSpent(couponUpdateDTO.getSpent());
            }
            if (couponUpdateDTO.getAccount() != null) {
                coupon.setAccount(couponUpdateDTO.getAccount());
            }
            couponRepository.save(coupon);
            return convertToDto(coupon);
        } else {
            throw new ResourceNotFoundException("Coupon with ID " + couponUpdateDTO.getId() + " not found");
        }
    }

    @Override
    public void deleteCouponById(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon with ID " + id + " not found");
        }
        couponRepository.deleteById(id);
    }

    @Override
    public List<CouponResponseDTO> getBySpentTrue() {
        return couponRepository.findBySpentTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponResponseDTO> getBySpentFalse() {
        return couponRepository.findBySpentFalse()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponResponseDTO> getExpiredCoupons() {
        return couponRepository.findExpiredCoupons()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponResponseDTO> getCurrentCoupons() {
        return couponRepository.findCurrentCoupons()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markCouponAsSpent(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon with ID " + couponId + " not found"));
        coupon.setSpent(true);
        couponRepository.save(coupon);
    }

    @Override
    public boolean isCouponSpent(Long couponId) {
        return couponRepository.isCouponSpent(couponId);
    }

    @Override
    public boolean isCouponExpired(Long couponId) {
        return couponRepository.isCouponExpired(couponId);
    }

    @Override
    public List<CouponResponseDTO> getValidCouponsByAccount(Long accountId, HttpServletRequest request) {
        try {
            boolean hasAccess = accountTokenUtils.hasAccessToAccount(request, accountId);
            if (!hasAccess) {
                throw new UnauthorizedException("Access denied to coupons of the account with ID " + accountId);
            }
            List<Coupon> accountCoupons = couponRepository.findByAccountId(accountId);
            List<Coupon> validCoupons = accountCoupons.stream()
                    .filter(coupon -> !coupon.isSpent() && coupon.getDueDate().isAfter(LocalDate.now()))
                    .collect(Collectors.toList());
            return validCoupons.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<CouponResponseDTO> getCouponsByAccount(Long accountId, HttpServletRequest request) {
        try {
            boolean hasAccess = accountTokenUtils.hasAccessToAccount(request, accountId);
            if (!hasAccess) {
                throw new UnauthorizedException("Access denied to coupons of the account with ID " + accountId);
            }
            List<Coupon> accountCoupons = couponRepository.findByAccountId(accountId);
            List<Coupon> validCoupons = accountCoupons.stream()
                    .toList();
            return validCoupons.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    private CouponResponseDTO convertToDto(Coupon coupon) {
        Long accountId = null;
        if (coupon.getAccount() != null) {
            accountId = coupon.getAccount().getId();
        }
        return new CouponResponseDTO(
                coupon.getId(),
                coupon.getIssueDate(),
                coupon.getDueDate(),
                coupon.getAmount(),
                coupon.getSpent(),
                accountId
        );
    }

    public Coupon convertResponseToEntity(CouponResponseDTO couponResponseDTO) {
        Account account = accountRepository.findByUserId(couponResponseDTO.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("No account associated with the coupon found"));
        Coupon coupon = new Coupon();
        coupon.setId(couponResponseDTO.getId());
        coupon.setIssueDate(couponResponseDTO.getIssueDate());
        coupon.setDueDate(couponResponseDTO.getDueDate());
        coupon.setAmount(couponResponseDTO.getAmount());
        coupon.setSpent(couponResponseDTO.getSpent());
        coupon.setAccount(account);
        return coupon;
    }

    @Override
    public double calculateCouponEffectiveness() {
        long totalCouponsCreated = couponRepository.count();
        long totalCouponsUsed = couponRepository.countBySpentTrue();

        if(totalCouponsCreated == 0) {
            return 0; // Evita dividir por cero
        }

        return ((double) totalCouponsUsed / totalCouponsCreated) * 100;
    }
}
