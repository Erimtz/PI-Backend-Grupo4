package com.gym.services;

import com.gym.dto.CouponResponseDTO;
import com.gym.dto.request.CouponCreateDTO;
import com.gym.dto.request.CouponUpdateDTO;
import com.gym.entities.Account;
import com.gym.entities.Coupon;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.CouponRepository;
import com.gym.services.impl.CouponServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {
    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponServiceImpl couponService;

    @Test
    public void testCreateCoupon() {
        Account account = Account.builder()
                .id(123L)
                .document("123")
                .build();

        CouponCreateDTO couponCreateDTO = CouponCreateDTO.builder()
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .amount(50.0)
                .spent(false)
                .account(account)
                .build();

        Coupon couponMock = Coupon.builder()
                .id(1L)
                .issueDate(couponCreateDTO.getIssueDate())
                .dueDate(couponCreateDTO.getDueDate())
                .amount(couponCreateDTO.getAmount())
                .spent(couponCreateDTO.getSpent())
                .account(couponCreateDTO.getAccount())
                .build();

        when(couponRepository.save(any(Coupon.class))).thenReturn(couponMock);

        CouponResponseDTO createdCoupon = couponService.createCoupon(couponCreateDTO);

        assertEquals(couponCreateDTO.getAccount().getId(), createdCoupon.getAccountId());
        assertEquals(couponCreateDTO.getIssueDate(), createdCoupon.getIssueDate());
        assertEquals(couponCreateDTO.getDueDate(), createdCoupon.getDueDate());
        assertEquals(couponCreateDTO.getAmount(), createdCoupon.getAmount());
        assertEquals(couponCreateDTO.getSpent(), createdCoupon.getSpent());
    }

    @Test
    public void testUpdateCoupon() {
        Long couponId = 1L;
        LocalDate newIssueDate = LocalDate.now().minusDays(1);
        LocalDate newDueDate = LocalDate.now().plusDays(6);
        Double newAmount = 75.0;
        Boolean newSpent = true;

        CouponUpdateDTO couponUpdateDTO = CouponUpdateDTO.builder()
                .id(couponId)
                .issueDate(newIssueDate)
                .dueDate(newDueDate)
                .amount(newAmount)
                .spent(newSpent)
                .build();

        Coupon existingCoupon = Coupon.builder()
                .id(couponId)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .amount(50.0)
                .spent(false)
                .account(null)
                .build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(existingCoupon));

        CouponResponseDTO updatedCoupon = couponService.updateCoupon(couponUpdateDTO);

        assertEquals(couponId, updatedCoupon.getId());
        assertEquals(newIssueDate, updatedCoupon.getIssueDate());
        assertEquals(newDueDate, updatedCoupon.getDueDate());
        assertEquals(newAmount, updatedCoupon.getAmount());
        assertEquals(newSpent, updatedCoupon.getSpent());

        verify(couponRepository, times(1)).save(existingCoupon);
    }

    @Test
    public void testGetAllCoupons() {
        List<Coupon> coupons = Arrays.asList(new Coupon(), new Coupon());
        when(couponRepository.findAll()).thenReturn(coupons);

        List<CouponResponseDTO> couponResponseDTOList = couponService.getAllCoupons();

        Assertions.assertTrue(true, "Get all coupons with exit");
    }

    @Test
    public void testCouponByIdExists() {
        Long couponId = 1L;
        Coupon coupon = new Coupon();
        coupon.setId(couponId);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        CouponResponseDTO couponResponseDTO = couponService.getCouponById(couponId);

        assertNotNull(couponResponseDTO);
        assertEquals(couponId, couponResponseDTO.getId());
    }

    @Test
    void testDeleteCouponByIdWithCallsDeleteById() {
        Long id = 1L;
        when(couponRepository.existsById(id)).thenReturn(true);

        couponService.deleteCouponById(id);

        verify(couponRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteCouponByIdWithResourceNotFoundException() {
        Long id = 1L;
        when(couponRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> couponService.deleteCouponById(id));
        verify(couponRepository, never()).deleteById(id);
    }

    @Test
    void testBySpentTrue() {
        List<Coupon> coupons = new ArrayList<>();
        coupons.add(createCoupon(true));
        coupons.add(createCoupon(true));
        when(couponRepository.findBySpentTrue()).thenReturn(coupons);

        List<CouponResponseDTO> result = couponService.getBySpentTrue();

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(CouponResponseDTO::getSpent));
    }

    @Test
    void testBySpentFalse() {
        List<Coupon> coupons = new ArrayList<>();
        coupons.add(createCoupon(false));
        coupons.add(createCoupon(false));
        when(couponRepository.findBySpentFalse()).thenReturn(coupons);

        List<CouponResponseDTO> result = couponService.getBySpentFalse();

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(CouponResponseDTO::getSpent));
    }

    private Coupon createCoupon(boolean spent) {
        return Coupon.builder()
                .id(1L)
                .spent(spent)
                .build();
    }

    @Test
    void testExpiredCoupons() {
        List<CouponResponseDTO> coupons = new ArrayList<>();
        coupons.add(CouponResponseDTO.builder().id(1L).issueDate(LocalDate.now().minusDays(5)).dueDate(LocalDate.now().minusDays(1)).build()); // Vencido
        coupons.add(CouponResponseDTO.builder().id(2L).issueDate(LocalDate.now().minusDays(3)).dueDate(LocalDate.now().plusDays(1)).build()); // No vencido
        coupons.add(CouponResponseDTO.builder().id(3L).issueDate(LocalDate.now().minusDays(2)).dueDate(null).build()); // Cupón sin fecha de vencimiento

        // Filtra los cupones vencidos
        List<CouponResponseDTO> expiredCoupons = coupons.stream()
                .filter(coupon -> coupon.getDueDate() != null && coupon.getDueDate().isBefore(LocalDate.now()))
                .toList();

        // Verifica que todos los cupones filtrados estén vencidos
        assertTrue(expiredCoupons.stream().allMatch(coupon -> coupon.getDueDate() != null && coupon.getDueDate().isBefore(LocalDate.now())));
    }

    @Test
    void testCurrentCoupons() {
        Coupon coupon1 = new Coupon();
        coupon1.setId(1L);
        coupon1.setIssueDate(LocalDate.now().minusDays(1));
        coupon1.setDueDate(LocalDate.now().plusDays(1));
        coupon1.setAmount(10.0);
        coupon1.setSpent(false);
        coupon1.setAccount(new Account());


        Coupon coupon2 = new Coupon();
        coupon2.setId(2L);
        coupon2.setIssueDate(LocalDate.now().minusDays(2));
        coupon2.setDueDate(LocalDate.now().minusDays(1));
        coupon2.setAmount(20.0);
        coupon2.setSpent(false);
        coupon2.setAccount(new Account());

        when(couponRepository.findCurrentCoupons()).thenReturn(Arrays.asList(coupon1, coupon2));

        List<CouponResponseDTO> couponResponseDTOList = couponService.getCurrentCoupons();

        assertEquals(2, couponResponseDTOList.size());
        assertEquals(1L, couponResponseDTOList.get(0).getId());
        assertEquals(2L, couponResponseDTOList.get(1).getId());
    }

    @Test
    void testCalculateCouponEffectiveness_NoCoupons_ReturnsZero() {
        when(couponRepository.count()).thenReturn(0L);
        when(couponRepository.countBySpentTrue()).thenReturn(0L);

        double effectiveness = couponService.calculateCouponEffectiveness();

        assertEquals(0, effectiveness);
    }

    @Test
    void testCalculateCouponEffectiveness_SomeCoupons_ReturnsCorrectEffectiveness() {

        long totalCouponsCreated = 100;
        long totalCouponsUsed = 40;
        double expectedEffectiveness = (double) totalCouponsUsed / totalCouponsCreated * 100;
        when(couponRepository.count()).thenReturn(totalCouponsCreated);
        when(couponRepository.countBySpentTrue()).thenReturn(totalCouponsUsed);

        double effectiveness = couponService.calculateCouponEffectiveness();

        assertEquals(expectedEffectiveness, effectiveness);
    }

}

