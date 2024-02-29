package com.gym.services;

import com.gym.dto.PurchaseDetailDTO;
import com.gym.entities.PurchaseDetail;
import com.gym.exceptions.DatabaseOperationException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.PurchaseDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseDetailServiceImpl implements PurchaseDetailService{

    private PurchaseDetailRepository purchaseDetailRepository;
    @Override
    public List<PurchaseDetailDTO> getAllPurchaseDetails() {
        return purchaseDetailRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PurchaseDetailDTO getPurchaseDetailById(Long id) {
        return purchaseDetailRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase details with ID " + id + " not found"));
    }

    @Override
    public PurchaseDetailDTO createPurchaseDetail(PurchaseDetailDTO purchaseDetailDTO) {
        try {
            PurchaseDetail purchaseDetail = PurchaseDetail.builder()
                    .quantity(purchaseDetailDTO.getQuantity())
                    .product(purchaseDetailDTO.getProduct())
                    .purchase(purchaseDetailDTO.getPurchase())
                    .build();
            purchaseDetailRepository.save(purchaseDetail);
            return convertToDto(purchaseDetail);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error occurred while saving purchase details", e);
        }
    }

    @Override
    public PurchaseDetailDTO updatePurchaseDetail(PurchaseDetailDTO purchaseRequest) {
        Optional<PurchaseDetail> purchaseDetailOptional = purchaseDetailRepository.findById(purchaseRequest.getId());
        if (purchaseDetailOptional.isPresent()){
            PurchaseDetail purchaseDetail = purchaseDetailOptional.get();

            if (purchaseRequest.getQuantity() != null) {
                purchaseDetail.setQuantity(purchaseRequest.getQuantity());
            }
            if (purchaseRequest.getProduct() != null) {
                purchaseDetail.setProduct(purchaseRequest.getProduct());
            }
            if (purchaseRequest.getPurchase() != null) {
                purchaseDetail.setPurchase(purchaseRequest.getPurchase());
            }
            purchaseDetailRepository.save(purchaseDetail);
            return convertToDto(purchaseDetail);
        } else {
            throw new ResourceNotFoundException("Purchase details with ID " + purchaseRequest.getId() + " not found");
        }
    }

    @Override
    public void deletePurchaseDetailById(Long id) {
        if (!purchaseDetailRepository.existsById(id)) {
            throw new ResourceNotFoundException("Purchase details with ID " + id + " not found");
        }
        purchaseDetailRepository.deleteById(id);
    }

    private PurchaseDetailDTO convertToDto(PurchaseDetail purchaseDetail) {
        return new PurchaseDetailDTO(
                purchaseDetail.getId(),
                purchaseDetail.getQuantity(),
                purchaseDetail.getProduct(),
                purchaseDetail.getPurchase()
        );
    }
}
