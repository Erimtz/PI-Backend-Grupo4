package com.gym.services.impl;

import com.gym.dto.request.PurchaseDetailRequestDTO;
import com.gym.entities.Product;
import com.gym.entities.PurchaseDetail;
import com.gym.repositories.ProductRepository;
import com.gym.repositories.PurchaseDetailRepository;
import com.gym.services.PurchaseDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseDetailServiceImpl implements PurchaseDetailService {

    private final PurchaseDetailRepository purchaseDetailRepository;
    private final ProductRepository productRepository;

    public PurchaseDetail createPurchaseDetail(PurchaseDetailRequestDTO requestDTO) {
        if (requestDTO.getProductId() == null) {
            throw new IllegalArgumentException("productId cannot be null");
        }

        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + requestDTO.getProductId()));

        PurchaseDetail purchaseDetail = new PurchaseDetail();
        purchaseDetail.setProduct(product);
        purchaseDetail.setQuantity(requestDTO.getQuantity());
        return purchaseDetailRepository.save(purchaseDetail);
    }

    public Double calculateSubtotal(PurchaseDetail purchaseDetail){
        return purchaseDetail.getProduct().getPrice() * purchaseDetail.getQuantity();
    }
}