package com.gym.services;

import com.gym.dto.PurchaseRequestDTO;
import com.gym.entities.Account;
import com.gym.entities.Product;
import com.gym.entities.Purchase;
import com.gym.entities.PurchaseDetail;
import com.gym.exceptions.UnauthorizedException;
import com.gym.exceptions.UserNotFoundException;
import com.gym.repositories.AccountRepository;
import com.gym.repositories.ProductRepository;
import com.gym.repositories.PurchaseRepository;
import com.gym.security.configuration.jwt.JwtUtils;
import com.gym.security.entities.UserEntity;
import com.gym.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService{

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    @Autowired
    private final AccountService accountService;

    public Purchase createPurchase(PurchaseRequestDTO requestDTO, String token) {
        Account account = accountService.getAccountFromToken(token);
        if (account == null) {
            throw new IllegalArgumentException("No se pudo obtener la cuenta del usuario");
        }
        Purchase purchase = new Purchase();
        purchase.setPurchaseDate(LocalDate.now());
        purchase.setAccount(account);

        List<PurchaseDetail> purchaseDetails = requestDTO.getPurchaseDetails().stream()
                .map(detailDTO -> {
                    PurchaseDetail detail = new PurchaseDetail();

                    if (detailDTO.getProduct() == null) {

                        throw new IllegalArgumentException("Product in PurchaseDetailDTO is null");
                    }
                    Long productId = detailDTO.getProduct().getId();

                    System.out.println("Searching for product with ID: " + productId);

                    Product product = productRepository.findById(detailDTO.getProduct().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + detailDTO.getProduct().getId()));

                    detail.setProduct(product);
                    detail.setQuantity(detailDTO.getQuantity());
                    return detail;
                })
                .collect(Collectors.toList());
        purchase.setPurchaseDetails(purchaseDetails);
        purchase.setCouponsApplied(requestDTO.getCouponsApplied());
        return purchaseRepository.save(purchase);
    }
}